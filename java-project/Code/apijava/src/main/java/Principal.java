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
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class Principal{

    public static void main(String... args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        Classroom classroom_service = ClassroomQuickstart.get_service();

        ListCoursesResponse response = classroom_service.courses().list()
                .setPageSize(10)
                .execute();
        List<Course> courses = response.getCourses();
        if (courses == null || courses.size() == 0) {
            System.out.println("No courses found.");
        } else {
            System.out.println("Courses:");
            for (Course course : courses) {
                System.out.printf("%s\n", course.getName());
            }
        }

        // Print the names and IDs for up to 10 files.
        Drive drive_service = DriveQuickstart.get_service();
        FileList result = drive_service.files().list()
                .setPageSize(10)
                .setFields("nextPageToken, files(id, name)")
                .execute();
        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
            }

        }
        
    }  
   
}