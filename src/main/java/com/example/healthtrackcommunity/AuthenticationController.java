package com.example.healthtrackcommunity;

import com.example.healthtrackcommunity.models.Doctor;
import com.example.healthtrackcommunity.models.Patient;
import config.DoctorDAO;
import config.PatientDAO;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javax.print.Doc;
import java.io.IOException;

public class AuthenticationController {

    public StackPane formsSection;
    public Button showPatientRegisterBtn;
    public Button showDoctorRegisterBtn;
    public Button showLoginBtn;

    //formulario de registro para doctor
    public VBox doctorRegisterForm;
    public TextField doctorEmailField;
    public TextField doctorNameField;
    public TextField doctorSpecializationField;
    public PasswordField doctorPasswordField;
    public Button registerDoctorBtn;

    //formulario de registro para paciente
    public VBox patientRegisterForm;
    public TextField patientEmailField;
    public TextField patientNameField;
    public PasswordField patientPasswordField;
    public Button registerPatientBtn;

    //formulario de login
    public VBox userLoginForm;
    public TextField loginEmailField;
    public PasswordField loginPasswordField;
    public Button loginBtn;

    //DAOS
    DoctorDAO doctorDAO;
    PatientDAO patientDAO;

    ObservableList<Doctor> doctors;
    ObservableList<Patient> patients;

    public void initialize() {
        showForm(userLoginForm);
        patientDAO = new PatientDAO();
        doctorDAO = new DoctorDAO();
        doctors = doctorDAO.getAll();
        patients = patientDAO.getAll();

        //DEBUG
        loginEmailField.setText("tristan.lira.1636@gmail.com");
        loginPasswordField.setText("password1");
    }

    /*********************MOSTRAR FORMULARIOS DE REGISTRO/INICIO DE SESION*******************************/

    public void showDoctorRegister(ActionEvent actionEvent) {
        showForm(doctorRegisterForm);
    }

    public void showPatientRegister(ActionEvent actionEvent) {
        showForm(patientRegisterForm);
    }

    public void showLogin(ActionEvent actionEvent) {
        showForm(userLoginForm);
    }

    private void showForm(Node showed) {
        for(Node i: formsSection.getChildren()) {
            i.setManaged(false);
            i.setVisible(false);
        }

        showed.setVisible(true);
        showed.setManaged(true);
    }

    /********************************** registro de pacientes y médicos ******************************************/

    boolean emptyStr(String str) {
        return str == null || str.isEmpty();
    }

    public void registerDoctor(ActionEvent actionEvent) {
        String email = doctorEmailField.getText();
        String password = doctorPasswordField.getText();
        String name = doctorNameField.getText();
        String specialization = doctorSpecializationField.getText();

        if (emptyStr(email) || emptyStr(name) || emptyStr(password)) {
            errorAlert("Campos vacíos", "Por favor rellene todos los campos.");
            return;
        }

        Doctor d = new Doctor(email, password, name, specialization);
        cleanDoctorForm();

        doctorDAO.create(d,
                () -> infoAlert("Médico creado.", "Médico registrado! Inicie sesión para entrar."),
                () -> errorAlert("Error al crear médico.", "El médico no fue creado. Compruebe que la contraseña tenga entre 6 y 24 caracteres y que su email sea correcto."),
                () -> errorAlert("Email en uso.", "El email indicado ya se encuentra en uso. Por favor utilice otro."));
    }

    private void cleanDoctorForm() {
        doctorEmailField.clear();
        doctorPasswordField.clear();
        doctorNameField.clear();
        doctorSpecializationField.clear();
    }

    public void registerPatient(ActionEvent actionEvent) {
        String email = patientEmailField.getText();
        String password = patientPasswordField.getText();
        String name = patientNameField.getText();

        if (emptyStr(email) || emptyStr(name) || emptyStr(password)) {
            errorAlert("Campos vacíos", "Por favor rellene todos los campos.");
            return;
        }

        Patient p = new Patient(email, password, name);
        cleanPatientForm();

        patientDAO.create(p,
                () -> infoAlert("Paciente creado.", "Paciente registrado! Inicie sesión para entrar."),
                () -> errorAlert("Error al crear paciente.", "El paciente no fue creado. Compruebe que la contraseña tenga entre 6 y 24 caracteres y que su email sea correcto."),
                () -> errorAlert("Email en uso.", "El email indicado ya se encuentra en uso. Por favor utilice otro."));
    }

    private void cleanPatientForm() {
        patientEmailField.clear();
        patientNameField.clear();
        patientPasswordField.clear();
    }


    /********************************** inicio de sesión ******************************************/

    public void login(ActionEvent actionEvent) throws IOException {
        String email = loginEmailField.getText();
        String password = loginPasswordField.getText();

        if (emptyStr(email) || emptyStr(password)) {
            errorAlert("Campos vacíos", "Por favor rellene todos los campos.");
            return;
        }

        //busca la cuenta en los médicos
        Doctor dLogged = null;
        for (Doctor i: doctors) {
            if (i.getEmail().equals(email) && i.getPassword().equals(password)) dLogged = i;
        }

        //busca la cuenta en los pacientes
        Patient pLogged = null;
        for(Patient i: patients) {
            if (i.getEmail().equals(email) && i.getPassword().equals(password)) pLogged = i;
        }

        if (dLogged == null && pLogged == null) {
            errorAlert("Cuenta inexistente.", "El email o la contraseña son incorrectos. Por favor inténtelo de nuevo.");
            return;
        }

        System.out.println(dLogged);
        System.out.println(pLogged);

        //decide que vista cargar según la cuenta obtenida
        if (dLogged != null) {
            loadDoctorView(actionEvent, dLogged);
        }
        else if (pLogged != null) {
            loadPatientView(actionEvent, pLogged);
        } else {
            //TODO cuenta de administrador
        }
    }

    private void loadDoctorView(ActionEvent event, Doctor logged) throws IOException {
        //cargar el fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("doctor-view.fxml"));
        Parent root = loader.load();

        //obtiene el controlador de la vista desde el loader e inicializa los datos del usuario loggeado
        DoctorController controller = loader.getController();
        controller.setLoggedUser(doctorDAO, logged);

        //obtiene el stage donde está el botón que creó el evento
        Scene currentScene = ((Node) event.getSource()).getScene();
        Stage stage = (Stage) currentScene.getWindow();
        Scene newScene = new Scene(root, currentScene.getWidth(), currentScene.getHeight());
        stage.setScene(newScene);
        stage.show();
    }

    private void loadPatientView(ActionEvent event, Patient logged) throws IOException {
        //cargar el fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("patient-view.fxml"));
        Parent root = loader.load();

        //obtiene el controlador de la vista desde el loader e inicializa los datos del usuario loggeado
        PatientController controller = loader.getController();
        controller.setLoggedUser(patientDAO, doctorDAO, logged);

        //obtiene el stage donde está el botón que creó el evento
        Scene currentScene = ((Node) event.getSource()).getScene();
        Stage stage = (Stage) currentScene.getWindow();
        Scene newScene = new Scene(root, currentScene.getWidth(), currentScene.getHeight());
        stage.setScene(newScene);
        stage.show();
    }


    /********************************** ALERTAS ******************************************/

    private void errorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.show();
    }

    private void infoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.show();
    }
}
