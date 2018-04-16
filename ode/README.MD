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
- wenn Service deployed wurde, lässt sich die entsprechende `wsdl` über das Axis2 Webinterface herunterladen
- wenn ein Parameter auf `locked` gestellt ist, verhindert dies die Änderung der Werte für alle Parameter (im Admin Panel)
- Beans müssen über Getter/Setter verfügen, sonst wird die wsdl nicht korrekt erzeugt
   - Felder können über Eintrag in der `service.xml` ignoriert werden
   ```
	<parameter name="beanPropertyRules">
		<bean class="sample.Address" excludeProperties="street,number" />
		<bean class="sample.AddressParent" excludeProperties="zipcode" />
	</parameter>
	```

# AXISFeedbackService
- Build über `mvn build`
- `FeedbackService.aar` aus Target Ordner in den Axis2 Services Ordner kopieren
   - Context Uri und FeedbackService Uri können im Admin Panel oder in der `service.xml` (innerhalb der aar) angepasst werden.
- sicherstellen, dass [Feedback-Service](https://github.com/IoTUDresden/feedback-service) läuft
- enthält auch einen einfachen Service, der das Senden von Commands an OpenHAB ermöglicht
- in der `service.xml` oder im Admin Panel lässt sich über die Service Class in den Debug Modus schalten. Einfach `Debug` an den Klassennamen anhängen. Es werden dann nur Nachrichten geloggt und keine Aktionen mit dem FBS oder OH ausgeführt 

# Modellierung Tips
- [HelloWorld](http://www-inf.it-sudparis.eu/SIMBAD/courses/doku.php?id=teaching_assistant:bpel:deploying_hello_world_bpel_process_with_eclipse_bpel_designer_and_apache_ode)
- nur Content der im Ordner `bpelContent` liegt, wird mit Server synchronisiert (Rechtsklick auf Server und dann auf Publish)

# Ausführen und Testen
- Sicherstellen, dass die [Eclipse Java EE Developer Tools](https://marketplace.eclipse.org/user/nitind/listings) installiert sind
- Rechtsklick auf `*.wsdl` &rarr; Webservices &rarr; Test with Web Service Explorer
   - beim Durchnavigieren der Services ggf. mal auf Refresh im Navigator klicken, falls die entsprechende Seite nicht geladen wird
   
# BPEL cheat sheet
- für die automatisch erstellte wsdl muss für den Process die richtige Adresse unter ODE angegeben werden &rarr; `http://localhost:8080/ode/processes/<ProcessName>`
- in `deploy.xml` müssen die entsprechenden Ports für die Links angegeben werden

## Aktionen
- `invoke` anderen WS aufrufen
- `receive` empfange von anderen Client 
- `reply` antworte anderen Client
- `assign` Variablen zuweisen, manipulieren
- `wait` warten
- `terminate` kompletten Prozess beenden
- `throw` Fehler und Ausnahmen anzeigen

## Kontrollkonstrukte
- `sequence` Aktivitäten, welche in geordneter Swquenz ausgeführt werden
- `flow` Aktivitäten, welche parallel ausgeführt werden
- `switch` um Branches zu implementieren
- `while` Schleife
- `pick` um einen von mehreren Pfaden zu wählen

## andere Services einbinden
- andere Services werden `partner Web service` genannt
- `partner links` geben jeweils an, welcher web service genutzt wird
   - Rollen können auch für jeden Partnerlink angegeben werden (eigene Rolle und Rolle des Partner Links)
   - Rollen können für jeden PortType angegeben werden (definiert die eig. Funktion etwas genauer, z.B. callback)
- `port type` definiert eine Reihe von Methoden, die über den Web Service erreichbar sind. Kann man auch mit Interface vergleichen.
   - `input` und `output` sind die entsprechenden Parameter die der Service benötigt
- `binding` beschreibt, wie der port type zu nutzen ist (z.B. SOAP Action). HTTP Binding (z.B. für POST) sollte eig. auch unterstützt werden.
- in `deploy.xml` muss der entsprechende Axis2 Endpoint angegeben werden &rarr; `...HttpSOAP11...` (sicherstellen, dass für diesen Endpoint auch die Adresse angegeben ist)


## Links
- [http://www.oracle.com/technetwork/articles/matjaz-bpel1-090575.html](http://www.oracle.com/technetwork/articles/matjaz-bpel1-090575.html)
- [https://docs.oracle.com/cd/E19182-01/821-0539/ggbxz/index.html](https://docs.oracle.com/cd/E19182-01/821-0539/ggbxz/index.html)
- [http://www-inf.it-sudparis.eu/cours/WebServices/TP_BPEL/installationBPELODE.html](http://www-inf.it-sudparis.eu/cours/WebServices/TP_BPEL/installationBPELODE.html)
- [https://www.codeproject.com/Articles/33746/An-Example-Of-BPEL-invoking-WebService](http://www-inf.it-sudparis.eu/cours/WebServices/TP_BPEL/installationBPELODE.html)