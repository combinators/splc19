package org.combinators.guidemo.domain;

import java.net.URL;

public class CoffeeBar {
    private String branchName;
    private URL logoLocation;

    private Database productDatabase;

    private MenuLayout menuLayout;

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public URL getLogoLocation() {
        return logoLocation;
    }

    public void setLogoLocation(URL logoLocation) {
        this.logoLocation = logoLocation;
    }

    public Database getProductDatabase() {
        return productDatabase;
    }

    public void setProductDatabase(Database productDatabase) {
        this.productDatabase = productDatabase;
    }

    public MenuLayout getMenuLayout() {
        return menuLayout;
    }

    public void setMenuLayout(MenuLayout menuLayout) {
        this.menuLayout = menuLayout;
    }
}
