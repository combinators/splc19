package org.combinators.guidemo;

import java.net.URL;
import javax.inject.Named;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

class DatabaseLocationModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Named("json database location")
  public String provideJSONDatabaseLocation() {
    return"http://localhost:9000/coffeebar/json/productoptions";
  }

  @Provides
  @Named("jdbc database location")
  public String provideJDBCDatabaseLocation() {
    return "jdbc:h2:tcp://localhost/mem:coffee";
  }

}
