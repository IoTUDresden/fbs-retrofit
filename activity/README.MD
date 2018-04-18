# Links
- [Activiti Homepage](https://www.activiti.org/)
- [Acitivit @ Github](https://github.com/activiti/activiti)

# Setup
- [Activiti herunterladen](https://www.activiti.org/download-bpm) 
- WebApps (die wars) zu Tomcat WebApps kopieren
- Delegate Klassen in `$TOMCAT_ROOT\webapps\activiti-app\WEB-INF\lib` kopieren
   - `mvn package` und die shaded jar aus dem target Ordner kopieren
- Host, Lampe etc. muss in der Delegat-Klasse angepasst werden
   
# Process ausführen
- einloggen bei `http://localhost:8080/activiti-app/`
   - User: `admin` Pwd: `test`
- Kickstart App &rarr; Import Process
- in Kickstart App &rarr; Apps &rarr; neue App Anlegen und Processmodell(e) hinzufügen &rarr; Publish
- in angelegter Webapp unter `Processes` den Prozess starten 