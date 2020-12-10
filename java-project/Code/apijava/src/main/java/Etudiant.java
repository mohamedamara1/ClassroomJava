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

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;

import java.io.File;
import java.nio.file.Path;


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

public class Etudiant extends Utilisateur{
	private Student student;
	private Classroom classroom_service;
	private Drive drive_service;


	public Etudiant(Classroom classroom_service, Drive drive_service){
		Student student = new Student();
		this.classroom_service = classroom_service;
		this.drive_service = drive_service;
	}

	public void download_everything() throws IOException, GeneralSecurityException{


                Map<String, ArrayList<String>> files_map = new HashMap<String, ArrayList<String>>();

		File wrapper_folder = new File("./ClassroomFolders");
		if (! wrapper_folder.exists())
			wrapper_folder.mkdir();

                ListCoursesResponse response = classroom_service.courses().list().setPageSize(5).execute();
                List<Course> courses = response.getCourses();	

                for (Course course : courses){
                        ArrayList<String> new_downloads = new ArrayList<String>();

                	String course_name = course.getName();
                	String course_id = course.getId();

                	String path = wrapper_folder+"/"+course_name;

                	File directory = new File(path);

                	if (! directory.exists()){
                		directory.mkdir();
                		(new File(path+"/Cours")).mkdir();
                		(new File(path+"/TD")).mkdir();
                		System.out.println(course_name + " Created.");
                	}



                        ListAnnouncementsResponse announcements_reponse = classroom_service.courses().announcements().list(course_id).execute();
                        List<Announcement> announcements = announcements_reponse.getAnnouncements();
                        System.out.println("Downloading files of "+course_name);
                        try{
                                new_downloads.addAll(download_announcements(announcements, course_name));
                        }
                        catch(NullPointerException e){
                                System.out.println("This Course does't have any announcements!");
                        }


                        ListCourseWorkResponse works_response = classroom_service.courses().courseWork().list(course_id).execute();
                        List<CourseWork> works = works_response.getCourseWork();
                        System.out.println("Downloading course works of "+course_name);
                        try{
                                new_downloads.addAll(download_works(works, course_name));
                        }

                        catch (NullPointerException e){
                                System.out.println("this course doesn't have any work!");
                        }

                        if (new_downloads.size() != 0)
                                files_map.put(course_name, new_downloads);


                }

                //printing newly downloaded files
                System.out.println("\n*********** Newly downloaded files for each course....... : **************\n");
                if ( files_map.size() == 0 ) 
                        System.out.println("None!");
                else
                {
                        try{
                                for (Map.Entry<String, ArrayList<String>> course: files_map.entrySet()){
                                        System.out.println("\n-------------"+course.getKey()+"-----------------\n");
                                        for( String name : course.getValue()){
                                                System.out.println(name);
                                        }
                                }

                        }
                        catch (NullPointerException e){

                        }                        
                }   

                System.out.println("------Total downloads size : " +super.humanReadableByteCountBin(super.download_size)+" -------");
	}

        public  ArrayList<String> download_announcements(List<Announcement> announcements, String course_name) throws NullPointerException{

                ArrayList<String> downloads = new ArrayList<String>();
                String saving_path = "./ClassroomFolders/"+course_name;

                List<File> files = new ArrayList<File>();
                List<String> files_names = new ArrayList<String>();

                super.listf(saving_path, files);

                for (File file : files){
                        files_names.add(file.getName());
                }

                for (Announcement announcement : announcements){
                        List<Material> materials = announcement.getMaterials();

                        try{
                                for (Material material : materials){
                                        String file_id = material.getDriveFile().getDriveFile().getId();
                                        String file_name = material.getDriveFile().getDriveFile().getTitle();
                                        if ( (!files_names.contains(file_name)) && verif(file_name)){
                                                System.out.println("Downloading : "+file_name);
                                                try{
                                                      super.file_download(file_id, file_name, saving_path, this.drive_service);
                                                      downloads.add(file_name);

                                                }
                                                catch(IOException e) {
                                                e.printStackTrace();
                                                }
                                        }
                                }
                        }
                        catch(NullPointerException e){
                        }
                }
                return downloads;               
        }

        public ArrayList<String> download_works(List<CourseWork> works, String course_name) throws NullPointerException{
                        ArrayList<String> downloads = new ArrayList<String>();

                        String saving_path = "./ClassroomFolders/"+course_name;
                        List<File> files = new ArrayList<File>();
                        super.listf(saving_path, files);      
                        
                        for (CourseWork work : works){
                                List<Material> materials = work.getMaterials();

                                try{
                                        for (Material material : materials){
                                                String file_id = material.getDriveFile().getDriveFile().getId();
                                                String file_name = material.getDriveFile().getDriveFile().getTitle();
                                                File file = new File(saving_path+"/"+file_name);
                                                if ( (!files.contains(file)) && verif(file_name)){
                                                        System.out.println("Downloading : "+file_name);
                                                        try{
                                                              super.file_download(file_id, file_name, saving_path, this.drive_service);
                                                              downloads.add(file_name);
                                                        }
                                                        catch(IOException e) {
                                                        e.printStackTrace();
                                                        }
                                                }
                                        }
                                }
                                catch(NullPointerException e){
                                }
                             
                }   
                return downloads;
        }

        public boolean verif(String file_name){
                String extension = file_name.substring(file_name.lastIndexOf(".") + 1);
                List<String> extensions = Arrays.asList("pdf", "docx", "pptx", "png", "jpg", "html", "css", "js", "java",
        "class", "txt", "r", "m", "sql", "doc", "mp3", "rar", "zip");
                return extensions.contains(extension);
        }

}