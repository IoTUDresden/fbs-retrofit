# Installiere Tomcat inkl. Ode und Axis2
- Download [Tomcat Server](https://tomcat.apache.org/download-90.cgi)
- Download [ODE War Distribution](http://ode.apache.org/getting-ode.html)
- Download [Axis2 War Distribution](https://axis.apache.org/axis2/java/core/download.html)
- War Distributionen nach `apache-tomcat-x.x.x/webapps` kopieren
- Server über `apache-tomcat-9.0.7-x.x.x/bin/startup.bat` starten
- Ode Web-Interface erreichbar über `localhost:8080/ode` 
- Axis2 Web-Interface erreichbar über `localhost:8080/axis2`
   - Admin Panel: admin, axis2   
   
# BPEL Editor (Eclipse)
- Eclipse von [hier](https://www.eclipse.org/downloads/) downloaden
- Designer von Update Site `http://download.eclipse.org/bpel/site/` in Eclipse installieren
- Ode Server hinzufügen
   - Tomcat Home angeben
   - Ode Home angeben (`${tomcat_path}/webapps/ode`)
   - unter Run-Configurations ggf. noch folgende Libs unter User Entries hinzufügen
      - `${tomcat_path/bin/tomcat-juli.jar}`
	  - `${tomcat_path/bin/commons-deamon.jar}`
      - `${tomcat_path/bin/bootstrap.jar}`
   - Ode Server sollte sich jetzt auch über Eclipse starten und stoppen lassen

# Axis2 Service
- Services deployen
   - `*.aar` Datei in `apache-tomcat-x.x.x/webapps/axis2/WEB-INF/services` kopieren
   - Services sind standardmäßig hot deployable
- [Beispiel Services](https://github.com/apache/axis2-java/tree/329ac88407e73d63c1831c85a1228abc588d8f39/modules/samples)

# AXISFeedbackService
- Build über `mvn build`
- `FeedbackService.aar` aus Target Ordner in den Axis2 Services Ordner kopieren
   - Context Uri und FeedbackService Uri können in der `service.xml` (innerhalb der aar) angepasst werden. Anpassung über das Web-Interface bringt leider einen Server Error
- sicherstellen, dass [Feedback-Service](https://github.com/IoTUDresden/feedback-service) läuft

