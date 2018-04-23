# YAWL
- Download [Yawl](http://www.yawlfoundation.org/)
- Unter Windows:
   - Im Control-Panel starten 
- Unter Linux
   - nicht getestet
- Basis Webadresse (Für Servcies) *Achtung Port sollte wegen OH geändert werden z.B. 9999*
   - http://localhost:9999/resourceService
   - User: admin PWD: YAWL
- Bevor Worklfows ausgeführt werden können, muss User angelegt werden

# Port ändern
- `YAWL_HOME/engine/apache-tomcat-x.x.x/conf/server.xml`
- `YAWL_HOME/bin/startup.bat` or `.sh`
- `YAWL_HOME/engine/apache/tomcat-x.x.x/webapps/yawl/WEB-INF/classes/defaultClients.properties`
- `YAWL_HOME/engine/apache/tomcat-x.x.x/webapps/yawl/WEB-INF/classes/org/yawlfoundation/yawl/engine/defaultClients.properties`
- All web.xml files under `YAWL_HOME/engine/apache-tomcat-x.x.x/webapp`
- im Editor unter File &rarr; Preferences und Plugins &rarr; Worklet Mgt &rarr; Settings


# YAWL MODEL
- "join" wenn task auf mehrere Eingangsflows warten soll (Join Types)
- "split" für mehrere Ausgangsflows (Split Types)
   - für "If Else" z.B. XOR verwenden  (XPATH Query)(test_1 ist Var Name)
   - `/Net/test_1/text() = '1'` für Var `test_1`. Für Zahlen  `number(/Net/test_1/text())`
- daten (input und output) müssen gemapped werden (bei decomposition)
   - Haken bei "Automated" setzen, wenn Daten automatisch erzeugt werden

# Services
- siehe `io.tudresden.fbs.yawl` für Implementierung und Projektsetup inkl. Maven (auch auf die web.xml achten)
- Run Maven Build (package)
- Anschließend Binaries (WEB-INF) aus `target` (z.B. aus dem Ordner `yawl.feedbackservice-0.0.1-SNAPSHOT`) zu `webapps/feedbackService` kopieren
- Dann im YAWL Web-Interface unter `Services` den Service anlegen (für Adresse z.B. `http://localhost/feedbackService/ib` bzw. `http://localhost/openhabService/ib`)

# FBS + OH 4 Yawl
- Zugangsdaten für Service `PWD = "1234" USER = "feedbackService"` bzw. `USER = openhabService`
- **Services sind für den Port `9999` konfiguriert**. Ändern in web.xml möglich.
  
