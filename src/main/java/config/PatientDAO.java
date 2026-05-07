package config;

import com.example.healthtrackcommunity.models.Patient;
import com.google.firebase.database.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.IllegalFormatCodePointException;
import java.util.regex.Pattern;

public class PatientDAO implements DAO <Patient> {

    private DatabaseReference ref;
    private ObservableList<Patient> patients;

    public PatientDAO() {
        ref = FirebaseConnection.getDB().getReference("patients");
        patients = FXCollections.observableArrayList();
        subscribe();
    }

    private void subscribe() {
        ref.addChildEventListener(new ChildEventListener() {
            /*el metodo remove usa equals para saber qué objeto eliminar. Ya que en todas las clases modelo un objeto es
            * igual a otro únicamente si su id es mismo, sin importar ningún otro atributo, se puede utilizar remove para
            * manipular la lista independientemente de los cambios realizados al nodo en la base de datos*/

            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                Patient p = snapshot.getValue(Patient.class);
                patients.add(p);
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                Patient p = snapshot.getValue(Patient.class);
                patients.remove(p);
                patients.add(p);
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                Patient p = snapshot.getValue(Patient.class);
                patients.remove(p);
            }

            @Override public void onChildMoved(DataSnapshot snapshot, String previousChildName) {}
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    @Override
    public ObservableList<Patient> getAll() {
        return patients;
    }

    @Override
    public Patient get(String id) {
        for (Patient i: patients) {
            if (i.getEmail().equals(id)) return i;
        }
        return null;
    }

    @Override
    public void create(Patient p) {
        if ( !(validateEmail(p.getEmail()) && validatePassword(p.getPassword())) ) {
            System.out.println("Email o contraseña incorrectos. No se creo la cuenta de paciente: " + p.getEmail());
            return;
        }

        if (patients.contains(p)) {
            System.out.println("Paciente existente, no se pudo crear la cuenta.");
            return;
        }

        ref.child(p.getEmail()).setValueAsync(p);
        System.out.println("Cuenta de paciente creada: " + p.getEmail());
    }

    @Override
    public void update(Patient p) {
        if (patients.contains(p)) {
            ref.child(p.getEmail()).setValueAsync(p);
        }
    }

    @Override
    public void delete(Patient p) {
        ref.child(p.getEmail()).removeValueAsync();
    }



    /* FUNCIONES CON CALLBACKS */

    public void create(Patient p, Runnable success, Runnable fail, Runnable emailUsed) {
        if ( !(validateEmail(p.getEmail()) && validatePassword(p.getPassword())) ) {
            System.out.println("Email o contraseña incorrectos. No se creo la cuenta de paciente: " + p.getEmail());
            fail.run();
            return;
        }

        if (patients.contains(p)) {
            System.out.println("Paciente existente, no se pudo crear la cuenta.");
            emailUsed.run();
            return;
        }

        ref.child(p.getEmail()).setValue(p, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference databaseReference) {
                if (error == null) {
                    System.out.println("Cuenta de paciente creada: " + p.getEmail());
                    success.run();
                } else {
                    System.out.println("No se pudo crear la cuenta de paciente: " + p.getEmail());
                    fail.run();
                }
            }
        });
    }


    /*VALIDACIONES*/

    private boolean validateEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern p = Pattern.compile(emailRegex);
        return email != null && p.matcher(email).matches();
    }

    private boolean validatePassword(String password) {
        String letters = "[a-zA-Z]+";
        String numbers = "[0-9]+";

        Pattern p1 = Pattern.compile(letters);
        Pattern p2 = Pattern.compile(numbers);

        return password != null &&
                p1.matcher(password).find() &&
                p2.matcher(password).find() &&
                password.length() >= 6 &&
                password.length() <= 24;
    }
}
