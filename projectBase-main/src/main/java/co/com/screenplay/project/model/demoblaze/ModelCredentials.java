package co.com.screenplay.project.model.demoblaze;

import io.cucumber.datatable.DataTable;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter

public class ModelCredentials {
    private String user;
    private String password;

    public ModelCredentials(DataTable credentials) {
        List<Map<String, String>> rows = credentials.asMaps(String.class, String.class);
        for (Map<String, String> columns : rows) {
            this.user = columns.get("user");
            this.password = columns.get("password");
        }
    }
}