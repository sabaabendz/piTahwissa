package tn.esprit.tahwissa.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import tn.esprit.tahwissa.entities.Voyage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AjouterReservationController implements Initializable {

    @FXML private TextField clientIdField;
    @FXML private ComboBox<Voyage> voyageCombo;
    @FXML private Spinner<Integer> personnesSpinner;
    @FXML private Label prixUnitaireLabel;
    @FXML private Label totalLabel;

    private List<Voyage> voyages;
    private Runnable onSaveCallback;

    public void setVoyages(List<Voyage> voyages) {
        this.voyages = voyages;
        voyageCombo.setItems(FXCollections.observableArrayList(voyages));
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configuration du spinner
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        personnesSpinner.setValueFactory(valueFactory);

        // Afficher le titre et le prix dans la combo
        voyageCombo.setConverter(new javafx.util.StringConverter<Voyage>() {
            @Override
            public String toString(Voyage v) {
                return v == null ? "" : v.getTitre() + " (" + v.getPrixUnitaire() + " DT)";
            }
            @Override
            public Voyage fromString(String s) { return null; }
        });

        // Écouteurs pour mettre à jour les labels
        voyageCombo.valueProperty().addListener((obs, oldV, newV) -> updateLabels());
        personnesSpinner.valueProperty().addListener((obs, oldN, newN) -> updateLabels());
    }

    private void updateLabels() {
        Voyage v = voyageCombo.getValue();
        if (v != null) {
            prixUnitaireLabel.setText(v.getPrixUnitaire() + " DT");
            int nb = personnesSpinner.getValue();
            totalLabel.setText((v.getPrixUnitaire() * nb) + " DT");
        } else {
            prixUnitaireLabel.setText("-");
            totalLabel.setText("-");
        }
    }

    public int getClientId() throws NumberFormatException {
        return Integer.parseInt(clientIdField.getText());
    }

    public Voyage getSelectedVoyage() {
        return voyageCombo.getValue();
    }

    public int getPersonnes() {
        return personnesSpinner.getValue();
    }

    public void saveSuccess() {
        if (onSaveCallback != null) onSaveCallback.run();
    }
}
