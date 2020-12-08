import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.*;
import com.google.api.services.classroom.Classroom;

;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.*;


import java.nio.file.Path;
import java.io.File;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Enseignant extends Utilisateur{
	private Teacher teacher;
	private Classroom classroom_service;
	private Drive drive_service;


	public Enseignant(Classroom classroom_service, Drive drive_service){
		Teacher teacher = new Teacher();
		this.classroom_service = classroom_service;
		this.drive_service = drive_service;
	}




    public void download_compte_rendu(String course_id, String work_id) throws IOException, GeneralSecurityException{

        String course_name = classroom_service.courses().get(course_id).execute().getName();
        String work_name = classroom_service.courses().courseWork().get(course_id, work_id).execute().getTitle();



        ListStudentSubmissionsResponse submissions_response = classroom_service.courses().courseWork().studentSubmissions().list(course_id, work_id).execute();
        List<StudentSubmission> submissions = submissions_response.getStudentSubmissions();  

        for (StudentSubmission submission : submissions){
            String student_name = classroom_service.courses().students().get(course_id, submission.getUserId()).execute().getProfile().getName().getFullName();

            File student_folder = new File("./ClassroomCompteRendu/"+course_name+"/"+work_name+"/"+student_name);
            if (! student_folder.exists())
                student_folder.mkdir(); 

            List<Attachment> attachments = submission.getAssignmentSubmission().getAttachments();
            for (Attachment attachment : attachments){
                DriveFile drive_file = attachment.getDriveFile();
                String drive_id = drive_file.getId();
                String drive_name = drive_file.getTitle();
                super.file_download(drive_id, drive_name, "./ClassroomCompteRendu/"+course_name+"/"+work_name+"/"+student_name, drive_service);
            }

        }
    }

  

	public void printppl() throws IOException, GeneralSecurityException{  
        
                File wrapper_folder = new File("./ClassroomCompteRendu");
                if (! wrapper_folder.exists())
                    wrapper_folder.mkdir();     

                ListCoursesResponse response = classroom_service.courses().list().execute();
                List<Course> courses = response.getCourses();	

                for (Course course : courses){

   

                    File course_folder = new File("./ClassroomCompteRendu/"+course.getName());
                    if (! course_folder.exists())
                        course_folder.mkdir();      

                	String course_name = course.getName();
                	String course_id = course.getId();


                    ListCourseWorkResponse works_response = classroom_service.courses().courseWork().list(course_id).execute();
                    List<CourseWork> works = works_response.getCourseWork();
                    System.out.println("Course name : "+course_name);
                    try{
                            for (CourseWork work : works){
                                String work_id = work.getId();
                                String work_name = work.getTitle();


                                File work_folder = new File("./ClassroomCompteRendu/"+course_name+"/"+work_name);
                                if (! work_folder.exists())
                                    work_folder.mkdir(); 

        
                                System.out.println("Downloading student submissions for work "+work_name+" in "+course_name);
                                try{
                                    download_compte_rendu(course_id, work_id);
                                }

                                catch (NullPointerException e){
                                        System.out.println("no submission!");
                                }


                            }                        
                    }
                    catch(NullPointerException e){
                        System.out.println("no course work");
                    }
                }


	}


        public boolean verif(String file_name){
                String extension = file_name.substring(file_name.lastIndexOf(".") + 1);
                List<String> extensions = Arrays.asList("pdf", "docx", "pptx", "png", "jpg", "html", "css", "js", "java",
        "class", "txt", "r", "m", "sql", "doc", "mp3", "rar", "zip");
                return extensions.contains(extension);
        }

}