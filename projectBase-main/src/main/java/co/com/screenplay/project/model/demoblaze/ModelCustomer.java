package co.com.screenplay.project.model.demoblaze;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter

public class ModelCustomer {
    private String name;
    private String lastname;
    private String zip;

    public ModelCustomer(List<List<String>> data) {
        this.name = data.get(1).get(0);
        this.lastname = data.get(1).get(1);
        this.zip = data.get(1).get(2);
    }
}