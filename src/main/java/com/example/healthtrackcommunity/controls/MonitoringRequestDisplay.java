package com.example.healthtrackcommunity.controls;

import com.example.healthtrackcommunity.models.Doctor;
import com.example.healthtrackcommunity.models.MonitoringRequest;
import com.example.healthtrackcommunity.models.Patient;
import javafx.scene.layout.HBox;

public class MonitoringRequestDisplay extends HBox {

    private final MonitoringRequest r;
    private final Patient p;

    public MonitoringRequestDisplay(MonitoringRequest r, Patient p) {
        this.r = r;
        this.p = p;

        build();
    }

    private void build() {

    }

    public boolean displaysRequest(MonitoringRequest r2) {
        if (r2.getId() == null || r2.getId().isEmpty()) return false;
        return r.getId().equals(r2.getId());
    }
}
