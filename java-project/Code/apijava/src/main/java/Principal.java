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

import com.google.api.services.drive.model.File;
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

import java.util.Scanner;
public class Principal{

    public static void main(String... args) throws IOException, GeneralSecurityException {

        // Build a new authorized API client service.
        System.out.println("Login:");
        Classroom classroom_service = ClassroomQuickstart.get_service();
        Drive drive_service = DriveQuickstart.get_service();
        int choice=0;

        Scanner sc = new Scanner(System.in);
        System.out.println("Are you a teacher or a student?");
        System.out.println("1- Student \n2- Teacher");

        choice = sc.nextInt();
        if (choice == 1){
            Etudiant e = new Etudiant(classroom_service, drive_service);
            e.display();
        }
        else if (choice == 2){
            Enseignant prof = new Enseignant(classroom_service, drive_service);
            prof.display();
        }
        else{
            System.out.println("Bad choice, please repeat again!");
        }
        sc.close();

    } 
   
}