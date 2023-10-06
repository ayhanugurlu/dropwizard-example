package com.au.example;

import com.au.example.core.Person;
import com.au.example.core.Template;
import com.au.example.db.PersonDAO;
import com.au.example.resources.HelloWorldResource;
import com.au.example.resources.PeopleResource;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;


public class DropwizardExampleApplication extends Application<DropwizardexampleConfiguration> {

    public static void main(final String[] args) throws Exception {
        new DropwizardExampleApplication().run(args);
    }

    private final HibernateBundle<DropwizardexampleConfiguration> hibernateBundle =
            new HibernateBundle<>(Person.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(DropwizardexampleConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };

    @Override
    public String getName() {
        return "dropwizard-example";
    }

    @Override
    public void initialize(Bootstrap<DropwizardexampleConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );



        bootstrap.addBundle(new MigrationsBundle<DropwizardexampleConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(DropwizardexampleConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });

        bootstrap.addBundle(hibernateBundle);

    }

    @Override
    public void run(final DropwizardexampleConfiguration configuration,
                    final Environment environment) {
        final Template template = configuration.buildTemplate();
        final PersonDAO dao = new PersonDAO(hibernateBundle.getSessionFactory());

        environment.jersey().register(new HelloWorldResource(template));
        environment.jersey().register(new PeopleResource(dao));
    }

}
