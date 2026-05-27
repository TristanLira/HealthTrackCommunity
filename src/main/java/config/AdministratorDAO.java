package config;

import com.example.healthtrackcommunity.models.Administrator;
import com.example.healthtrackcommunity.models.Doctor;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.regex.Pattern;

public class AdministratorDAO implements DAO<Administrator> {

    private DatabaseReference ref;

    private ObservableList<Administrator> administrators;

    public AdministratorDAO() {
        ref = FirebaseConnection.getDB().getReference("administrators");

        administrators = FXCollections.observableArrayList();

        subscribe();
    }

    private void subscribe() {

        ref.addChildEventListener(
                new ChildEventListener() {

                    @Override
                    public void onChildAdded(
                            DataSnapshot snapshot,
                            String previousChildName
                    ) {

                        Administrator a =
                                snapshot.getValue(
                                        Administrator.class
                                );

                        administrators.add(a);
                    }

                    @Override
                    public void onChildChanged(
                            DataSnapshot snapshot,
                            String previousChildName
                    ) {

                        Administrator a =
                                snapshot.getValue(
                                        Administrator.class
                                );

                        administrators.remove(a);
                        administrators.add(a);
                    }

                    @Override
                    public void onChildRemoved(
                            DataSnapshot snapshot
                    ) {

                        Administrator a =
                                snapshot.getValue(
                                        Administrator.class
                                );

                        administrators.remove(a);
                    }

                    @Override
                    public void onChildMoved(
                            DataSnapshot snapshot,
                            String previousChildName
                    ) {}

                    @Override
                    public void onCancelled(
                            DatabaseError error
                    ) {}
                });
    }

    @Override
    public ObservableList<Administrator> getAll() {
        return administrators;
    }

    @Override
    public Administrator get(String id) {

        for (Administrator a :
                administrators) {

            if (a.getId().equals(id)) {
                return a;
            }
        }

        return null;
    }

    @Override
    public void create(
            Administrator a
    ) {

        if (!(validateEmail(
                a.getEmail()
        ) && validatePassword(
                a.getPassword()
        ))) {

            System.out.println(
                    "Email o contraseña incorrectos. " +
                            "No se creó la cuenta de administrador: "
                            + a.getEmail()
            );

            return;
        }

        if (emailRegistered(a)) {

            System.out.println(
                    "Administrador existente, " +
                            "no se pudo crear la cuenta."
            );

            return;
        }

        DatabaseReference pushed =
                ref.push();

        a.setId(pushed.getKey());

        pushed.setValueAsync(a);

        System.out.println(
                "Cuenta de administrador creada: "
                        + a.getEmail()
        );
    }

    private boolean emailRegistered(
            Administrator a
    ) {

        for (Administrator i :
                administrators) {

            if (i.getEmail()
                    .equals(a.getEmail())) {

                return true;
            }
        }

        return false;
    }

    @Override
    public void update(
            Administrator a
    ) {

        if (!administrators.contains(a))
            return;

        ref.child(a.getId())
                .setValueAsync(a);
    }

    @Override
    public void delete(
            Administrator a
    ) {

        if (!administrators.contains(a))
            return;

        ref.child(a.getId())
                .removeValueAsync();
    }


    //FUNCIONES CON CALLBACKS

    public void create(
            Administrator a,
            Runnable success,
            Runnable fail,
            Runnable emailUsed
    ) {

        if (!(validateEmail(a.getEmail()) && validatePassword(a.getPassword()))) {
            System.out.println("Email o contraseña incorrectos. " + "No se creó la cuenta: " + a.getEmail());

            fail.run();
            return;
        }

        if (emailRegistered(a)) {

            System.out.println(
                    "Administrador existente, " +
                            "no se pudo crear la cuenta."
            );

            emailUsed.run();
            return;
        }

        DatabaseReference pushed = ref.push();

        a.setId(pushed.getKey());

        pushed.setValue(a, new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(DatabaseError error,DatabaseReference databaseReference) {

                if (error == null) {

                    System.out.println("Cuenta de administrador creada: " + a.getEmail());
                    success.run();

                } else {

                    System.out.println("No se pudo crear la cuenta: " + a.getEmail());

                    fail.run();
                }
            }
        });
    }


    //validaciones

    private boolean validateEmail(
            String email
    ) {

        String emailRegex =
                "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@"
                        + "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern p =
                Pattern.compile(emailRegex);

        return email != null
                && p.matcher(email)
                .matches();
    }

    private boolean validatePassword(
            String password
    ) {

        String letters =
                "[a-zA-Z]+";

        String numbers =
                "[0-9]+";

        Pattern p1 =
                Pattern.compile(letters);

        Pattern p2 =
                Pattern.compile(numbers);

        return password != null
                && p1.matcher(password)
                .find()
                && p2.matcher(password)
                .find()
                && password.length() >= 6
                && password.length() <= 24;
    }
}