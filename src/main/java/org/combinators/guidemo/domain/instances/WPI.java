package org.combinators.guidemo.domain.instances;

import org.combinators.guidemo.domain.CoffeeBar;
import org.combinators.guidemo.domain.Database;
import org.combinators.guidemo.domain.DatabaseType;
import org.combinators.guidemo.domain.MenuLayout;

import java.net.MalformedURLException;
import java.net.URL;

public class WPI extends CoffeeBar {
    public WPI() {
        try {
            super.setBranchName("Finest Coffee @ WPI");
            super.setLogoLocation(new URL("https://upload.wikimedia.org/wikipedia/en/thumb/1/1b/WPI_logo.png/150px-WPI_logo.png"));
            super.setMenuLayout(MenuLayout.DropDown);

            Database db = new Database();
            db.setDatabaseLocation(new URL("http://localhost:9000/coffeebar/json/productoptions"));
            db.setDatabaseType(DatabaseType.RestJSON);
            super.setProductDatabase(db);

        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
