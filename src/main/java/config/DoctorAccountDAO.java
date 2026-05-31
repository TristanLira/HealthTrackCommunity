package config;

import com.example.healthtrackcommunity.models.Doctor;
import com.example.healthtrackcommunity.models.DoctorAccountRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.regex.Pattern;


public class DoctorAccountDAO implements DAO<DoctorAccountRequest> {

    private DatabaseReference ref;

    private ObservableList<DoctorAccountRequest> requests;

    // Lista de doctores existente para validar emails
    private ObservableList<Doctor> doctors;

    public DoctorAccountDAO(ObservableList<Doctor> doctors) {
        this.doctors = doctors;

        ref = FirebaseConnection.getDB().getReference("doctorAccountRequests");
        requests = FXCollections.observableArrayList();

        subscribe();
    }

    private void subscribe() {
        ref.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                DoctorAccountRequest request = snapshot.getValue(DoctorAccountRequest.class);
                requests.add(request);
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                DoctorAccountRequest request = snapshot.getValue(DoctorAccountRequest.class);
                requests.remove(request);
                requests.add(request);
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                DoctorAccountRequest request = snapshot.getValue(DoctorAccountRequest.class);
                requests.remove(request);
            }

            @Override public void onChildMoved(DataSnapshot snapshot, String previousChildName) {}
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    @Override
    public ObservableList<DoctorAccountRequest> getAll() {
        return requests;
    }

    @Override
    public DoctorAccountRequest get(String id) {
        for (DoctorAccountRequest request : requests) {
            if (request.getId().equals(id)) return request;
        }
        return null;
    }

    @Override
    public void create(DoctorAccountRequest request) {
        if (!(validateEmail(request.getEmail()) && validatePassword(request.getPassword()))) {
            System.out.println("Email o contraseña inválidos. No se creó la solicitud.");
            return;
        }

        if (emailRegistered(request.getEmail())) {
            System.out.println("Ya existe un doctor con ese email.");
            return;
        }

        DatabaseReference pushed = ref.push();
        request.setId(pushed.getKey());
        pushed.setValueAsync(request);

        System.out.println("Solicitud de cuenta creada para: " + request.getEmail());
    }

    @Override
    public void update(DoctorAccountRequest request) {
        if (!requests.contains(request)) return;
        ref.child(request.getId()).setValueAsync(request);
    }

    @Override
    public void delete(DoctorAccountRequest request) {
        if (!requests.contains(request)) return;
        ref.child(request.getId()).removeValueAsync();
    }

    /* VERSIONES CON CALLBACKS */

    public void create(DoctorAccountRequest request, Runnable success, Runnable fail, Runnable emailUsed) {

        if (!(validateEmail(request.getEmail()) && validatePassword(request.getPassword()))) {
            fail.run();
            return;
        }

        if (emailRegistered(request.getEmail())) {
            emailUsed.run();
            return;
        }

        DatabaseReference pushed = ref.push();
        request.setId(pushed.getKey());

        pushed.setValue(request, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(
                    DatabaseError error,
                    DatabaseReference databaseReference) {
                if (error == null) {
                    success.run();
                } else {
                    fail.run();
                }
            }
        });
    }



    /*valida si el email ya pertenece a un Doctor o a una solicitud*/
    private boolean emailRegistered(String email) {
        for (Doctor i : doctors) {
            if (i.getEmail() != null && i.getEmail().equalsIgnoreCase(email)) return true;
        }

        for (DoctorAccountRequest i: requests) {
            if (i.getEmail() != null && i.getEmail().equalsIgnoreCase(email)) return true;
        }

        return false;
    }

    /* VALIDACIONES */

    private boolean validateEmail(String email) {
        String emailRegex =
                "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@"
                        + "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern p = Pattern.compile(emailRegex);

        return email != null && p.matcher(email).matches();
    }

    private boolean validatePassword(String password) {
        String letters = "[a-zA-Z]+";
        String numbers = "[0-9]+";

        Pattern p1 = Pattern.compile(letters);
        Pattern p2 = Pattern.compile(numbers);

        return password != null
                && p1.matcher(password).find()
                && p2.matcher(password).find()
                && password.length() >= 6
                && password.length() <= 24;
    }
}
