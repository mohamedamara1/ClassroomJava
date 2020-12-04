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
import java.util.List;


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


		File wrapper_folder = new File("./ClassroomFolders");
		if (! wrapper_folder.exists())
			wrapper_folder.mkdir();
		

        ListCoursesResponse response = classroom_service.courses().list().setPageSize(10).execute();
        List<Course> courses = response.getCourses();		
        for (Course course : courses){
        	String course_name = course.getName();
        	String course_id = course.getId();

        	String path = wrapper_folder+"/"+course_name;

        	File directory = new File(path);
        	File td = new File(path+"/Cours");
        	File cours = new File(path+"/TD");

        	if (! directory.exists()){
        		directory.mkdir();
        		td.mkdir();
        		cours.mkdir();

        		System.out.println(course_name + " Created.");
        	}
        }


	}
}

