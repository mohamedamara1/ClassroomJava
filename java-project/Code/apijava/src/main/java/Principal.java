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

import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import java.util.Scanner;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
 
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class Principal{
    public static String class_path;
    public static String drive_path;

    public static void main(String... args)  {
 
         
        credentials_creation();

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
         
        public void run() {
         try{
            createAndShowGUI(); 
 
         }
         catch (IOException | GeneralSecurityException e){
            System.out.println(e);      }
         
        }
         
          });
  

    } 
public static void credentials_creation(){
    
        java.io.File credentials_folder = new java.io.File("./credentials");
        if (! credentials_folder.exists()){
            credentials_folder.mkdir();  
            System.out.println("Credentials folder created!!!! \n please insert the credentials files in that credentials folder, under the name credentials-drive.json and credentials-classroom.json ");
                }

        
    }
private static final long serialVersionUID = 1L;

private static void createAndShowGUI() throws IOException, GeneralSecurityException{

    // Create and set up the window.
    final JFrame frame = new JFrame("Centered");

    // Display the window.
    frame.setSize(200, 200);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // set flow layout for the frame
    frame.getContentPane().setLayout(new FlowLayout());

  //  JButton button = new JButton("Choose file/directory");

    //button.addActionListener(new ActionListener() {
      //  @Override
        //public void actionPerformed(ActionEvent e) {
    createFileChooser(frame, 0);
    createFileChooser(frame, 1);
    System.out.println(class_path + drive_path);

          // Build a new authorized API client service.
        if ((class_path != null) && (drive_path != null)){
        System.out.println("MAIN CALL : "+ class_path +" " + drive_path);


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

        //}
    //});

    //frame.getContentPane().add(button);

}

private static void createFileChooser(final JFrame frame, int x) {
    if (x == 0){
    String filename = File.separator+".";
    JFileChooser fileChooser = new JFileChooser(new File(filename));

    // pop up an "Open File" file chooser dialog
    fileChooser.showOpenDialog(frame);
    java.io.File class_json = fileChooser.getSelectedFile();
    
    if (class_json != null){
        class_path = class_json.getPath();
        System.out.println("class-credentials.json PATH : "+class_path);
    }

}
else{ 
    String filename = File.separator+"tmp";
    JFileChooser fileChooser = new JFileChooser(new File(filename));

    // pop up an "Open File" file chooser dialog
    fileChooser.showOpenDialog(frame);
    java.io.File drive = fileChooser.getSelectedFile();
    
    if (drive != null){
        drive_path = drive.getPath();
        System.out.println("drive-credentials.json PATH : "+drive_path);
    }

  //  System.out.println("drive-credentials.json: " + fileChooser.getSelectedFile());
   // this.class_path=fileChooser.getSelectedFile().getName();    

}

}

}