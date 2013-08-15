package com.clarkparsia.play.imperium;

import play.*;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.clarkparsia.empire.Empire;
import com.clarkparsia.empire.EmpireException;
import com.clarkparsia.empire.config.EmpireConfiguration;
import com.clarkparsia.empire.config.ConfigKeys;
import com.clarkparsia.empire.config.io.ConfigReader;
import com.clarkparsia.empire.config.io.impl.PropertiesConfigReader;
import com.clarkparsia.empire.config.io.impl.XmlConfigReader;
import com.clarkparsia.empire.util.EmpireModule;
import com.clarkparsia.empire.util.DefaultEmpireModule;

import com.clarkparsia.empire.sesametwo.OpenRdfEmpireModule;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

/**
 * An example Play 2 plugin written in Java.
 */
public class EmpirePlugin extends Plugin
{
    private final Application application;

    public ImperiumPlugin(Application application)
    {
        this.application = application;
    }

    @Override
    public void onStart()
    {
        Configuration configuration = application.configuration();
        // you can now access the application.conf settings, including any custom ones you have added
        Logger.info("EmpirePlugin has started");
        
        // TODO: consider moving this to the onConfigurationRead event
        super.onStart();

        Thread.currentThread().setContextClassLoader(application.classloader());

        Map<String, String> aConfig = new HashMap<String, String>();

        for (Object aObj : configuration.keys()) {
            String aKey = aObj.toString();

            if (aKey.startsWith("empire.")) {
                aConfig.put(aKey.substring(7), configuration.getString(aKey));
            }
        }

        Collection<EmpireModule> aModules = new ArrayList<EmpireModule>();

        if (aConfig.containsKey("support")) {
            String aSupport = aConfig.get("support");

            for (String aClassName : aSupport.split("(,\\w)")) {
                try {
                    aModules.add( (EmpireModule) Class.forName(aClassName.trim()).newInstance());
                }
                catch (ClassCastException e) {
                    play.Logger.error("You must specify a class which is a compatible Empire Guice Module.", e);
                }
                catch (Exception e) {
                    play.Logger.error("Error while loading a support class.", e);
                }
            }
        }

        if (aModules.isEmpty()) {
            // sesame will be the default
            aModules.add(new OpenRdfEmpireModule());
        }

        Map<String, String> aGlobalConfig = new HashMap<String, String>();
//      aGlobalConfig.put(ConfigKeys.ANNOTATION_INDEX, "empire.config");

        EmpireConfiguration aEmpireConfig = null; //DefaultEmpireModule.readConfiguration();

        if (aEmpireConfig == null) {
            aEmpireConfig = new EmpireConfiguration(aGlobalConfig,
                    Collections.singletonMap("imperium", aConfig));

            try {
                if (Play.application().configuration().getString("empire.config") != null) {
                    String aPath = Play.application().configuration().getString("empire.config");

                    ConfigReader aReader = new PropertiesConfigReader();
                    if (aPath.endsWith("xml")) {
                        aReader = new XmlConfigReader();
                    }

                    aEmpireConfig = aReader.read(new FileInputStream(aPath));
                }
                else if (Play.application().getFile("conf/empire.configuration") != null &&
                        Play.application().getFile("conf/empire.configuration").exists()) {
                    aEmpireConfig = new PropertiesConfigReader().read(new FileInputStream(Play.application().getFile("conf/empire.configuration")));

                }
            }
            catch (IOException e) {
                e.printStackTrace();
                play.Logger.error("empire config load error", e);
            }
            catch (EmpireException e) {
                play.Logger.error("empire config load error", e);
            }
        }


        Empire.init(aEmpireConfig,
                aModules.toArray(new EmpireModule[aModules.size()]));

    }

    @Override
    public void onStop()
    {
        // you may want to tidy up resources here
        Logger.info("MyExamplePlugin has stopped");
    }
}