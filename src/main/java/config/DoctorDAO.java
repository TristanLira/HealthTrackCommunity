package config;

import com.example.healthtrackcommunity.models.Doctor;
import com.example.healthtrackcommunity.models.Patient;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.print.Doc;
import java.util.regex.Pattern;


public class DoctorDAO implements DAO <Doctor> {

    private DatabaseReference ref;

    private ObservableList<Doctor> doctors;

    public DoctorDAO() {
        ref = FirebaseConnection.getDB().getReference("doctors");
        subscribe();
    }

    private void subscribe() {
        doctors = FXCollections.observableArrayList();

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                Doctor d = snapshot.getValue(Doctor.class);
                doctors.add(d);
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                Doctor d = snapshot.getValue(Doctor.class);

                /*remove usa equals para encontrar que elemento eliminar. Ya que en equals de doctor se hace la comparación
                * únicamente por el id (email), se puede eliminar el objeto anterior y agregar el actualizado solo con remove.*/
                doctors.remove(d);
                doctors.add(d);
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                Doctor d = snapshot.getValue(Doctor.class);
                doctors.remove(d);
            }

            @Override public void onChildMoved(DataSnapshot snapshot, String previousChildName) {}
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    @Override
    public ObservableList<Doctor> getAll() {
        return doctors;
    }

    @Override
    public Doctor get(String id) {
        for (Doctor d: doctors) {
            if (d.getId().equals(id)) return d;
        }
        return null;
    }

    @Override
    public void create(Doctor d) {
        if ( !(validateEmail(d.getEmail()) && validatePassword(d.getPassword())) ) {
            System.out.println("Email o contraseña incorrectos. No se creo la cuenta de medico" + d.getEmail());
            return;
        }

        if (emailRegistered(d)) {
            System.out.println("Medico existente, no se pudo crear la cuenta.");
            return;
        }

        DatabaseReference pushed = ref.push();
        d.setId(pushed.getKey());
        pushed.setValueAsync(d);
        System.out.println("Cuenta de medico creada: " + d.getEmail());
    }

    private boolean emailRegistered(Doctor d) {
        for (Doctor i: doctors) {
            if (i.getEmail().equals(d.getEmail())) return true;
        }
        return false;
    }

    @Override
    public void update(Doctor d) {
        if (!doctors.contains(d)) return;
        ref.child(d.getId()).setValueAsync(d);
    }

    @Override
    public void delete(Doctor d) {
        ref.child(d.getId()).removeValueAsync();
    }



    /* FUNCIONES CON CALLBACKS */

    public void create(Doctor d, Runnable success, Runnable fail, Runnable emailUsed) {
        if ( !(validateEmail(d.getEmail()) && validatePassword(d.getPassword())) ) {
            System.out.println("Email o contraseña incorrectos. No se creo la cuenta de medico" + d.getEmail());
            fail.run();
            return;
        }

        if (emailRegistered(d)) {
            System.out.println("Medico existente, no se pudo crear la cuenta.");
            emailUsed.run();
            return;
        }

        DatabaseReference pushed = ref.push();
        d.setId(pushed.getKey());
        pushed.setValue(d, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference databaseReference) {
                if (error == null) {
                    System.out.println("Cuenta de medico creada: " + d.getEmail());
                    success.run();
                } else {
                    System.out.println("No se pudo crear la cuenta de paciente: " + d.getEmail());
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
