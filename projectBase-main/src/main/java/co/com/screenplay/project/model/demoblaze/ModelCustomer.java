package co.com.screenplay.project.model.demoblaze;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

    /**
     * Constructor alternativo para usar con Scenario Outline,
     * donde los valores se obtienen directamente como parámetros de tipo String.
     *
     * @param name     nombre del cliente
     * @param lastname apellido del cliente
     * @param zip      código postal del cliente
     */
    public ModelCustomer(String name, String lastname, String zip) {
        this.name = name;
        this.lastname = lastname;
        this.zip = zip;
    }
}