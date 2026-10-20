# 

# GK71 Flood Monitoring

Autor: Khasu Akhmadov

## 1 Projektinitialisierung und Architektur

Zu Beginn wurde ein neues Spring Boot Projekt mit Gradle aufgesetzt. Die Architektur wurde in klar getrennte Bereiche aufgeteilt. Dazu gehören das Paket Model für die Datenklassen, das Paket Repository für den Datenzugriff, der Service Layer für die Geschäftslogik, die Controller für die HTTP Anfragen und ein Exception Handler für die zentrale Fehlerbehandlung. 

In der Datei build.gradle wurden die notwendigen Bibliotheken hinterlegt. Dies umfasste Spring Web für die REST Schnittstelle, Jackson XML für die XML Unterstützung, Spring Boot Starter Validation für die Prüfung von Parametern, Springdoc OpenAPI für die Dokumentation und Spring Boot Starter Test für die automatisierten Tests. 

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'com.fasterxml.jackson.dataformat:jackson-dataformat-xml'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

Während dieser Phase trat ein Fehler auf, da die XML Bibliothek von der Entwicklungsumgebung nicht sofort erkannt wurde. Dieses Problem konnte durch eine Synchronisierung des Gradle Projektes und das Neuladen der Entwicklungsumgebung behoben werden.

## 2 Datenmodelle Repositories und Simulation

Im nächsten Schritt wurden die Datenmodelle implementiert. Die Klasse Station enthält Felder wie Identifikation, Name, Zeitstempel, Wasserstand und Einheit. Die Klasse Measurement speichert historische Messwerte. Zusätzlich wurden eine Enum Klasse für die Warnstufen Normal, Hard und Critical sowie eine Klasse für Fehlerantworten erstellt. 

Die Repositories übernehmen die Datenhaltung im Arbeitsspeicher. Der Service Layer enthält die Kernlogik. Dazu gehört die serverseitige Berechnung der Warnstufen basierend auf den Wasserständen sowie eine zeitgesteuerte Simulation. Diese Simulation generiert in einem festen Intervall neue Messwerte und aktualisiert die Stationen. Für die Tests lässt sich diese Simulation über eine Eigenschaft in der Konfigurationsdatei deaktivieren.

```java
public void addMeasurement(Measurement m) {
    if (m.getWaterLevel() >= CRITICAL_LEVEL) {
        m.setWarningLevel(WarningLevel.CRITICAL);
    } else if (m.getWaterLevel() >= WARN_LEVEL) {
        m.setWarningLevel(WarningLevel.HARD);
    } else {
        m.setWarningLevel(WarningLevel.NORMAL);
    }
    measurementRepo.save(m);
}
```

## 3 REST Controller und Fehlerbehandlung

Der Controller verarbeitet die eingehenden HTTP Anfragen. Es wurden Endpunkte für das Abrufen aller Stationen, einer spezifischen Station, des aktuellen Messwerts sowie historischer Daten mit Filtern für Zeitraum und Limit implementiert. Zusätzlich wurden Endpunkte für die Statistik und die Filterung nach Warnstufen hinzugefügt. Die API liefert standardmäßig JSON und unterstützt XML über den Accept Header.

Eine zentrale Fehlerbehandlungsklasse fängt Ausnahmen ab. Dazu gehören unbekannte Stationen, ungültige Zeiträume, negative Limits oder nicht unterstützte Formate. Der Client erhält daraufhin eine saubere Fehlermeldung ohne interne Systemdetails oder detaillierte Stacktraces. 

```java
@ExceptionHandler(StationNotFoundException.class)
public ResponseEntity<ErrorResponse> handleNotFound(StationNotFoundException ex, HttpServletRequest req) {
    return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), req);
}
```

## 4 Der Webclient

Für die Benutzeroberfläche wurde ein Webclient mit HTML, Cascading Style Sheets und jQuery erstellt. Dieser lädt die Stationen, zeigt den aktuellen Messwert an und stellt die historischen Daten in einer Tabelle dar. Die Warnstufen werden farblich hervorgehoben. 

Während der Entwicklung gab es zwei kleinere Fehler im Client. Das Dropdown Menü zeigte anfangs den Wert undefined an, was durch die Korrektur des Attributnamens im JavaScript behoben wurde. Zudem blieb die Tabelle bei der XML Ansicht leer, was durch eine Anpassung des XML Parsers gelöst wurde. Beide Formate funktionieren nun einwandfrei.

```javascript
// Anpassung des XML Parsers
$(xml).find('item, measurement').each(function() {
    measurements.push(parseXmlMeasurement(this));
});
```

## 5 Automatisierte Tests und Entwicklungsumgebung

Zur Qualitätssicherung wurden automatisierte Tests für die JSON Ausgabe, die XML Ausgabe, die Fehlerbehandlung und die Filterung geschrieben. Die Testklasse wurde zunächst im falschen Verzeichnis abgelegt, was zu einem Kompilierungsfehler in der Entwicklungsumgebung führte. Nach dem Verschieben in das korrekte Testverzeichnis trat ein weiterer Fehler auf, bei dem die Entwicklungsumgebung die Klasse nicht finden konnte. 

Die Lösung bestand darin, die Tests über das Terminal mit dem Gradle Befehl auszuführen. Der Befehl bereinigt das Projekt, kompiliert den Code neu und führt alle Tests aus. Am Ende stand die Meldung BUILD SUCCESSFUL, was bedeutet, dass alle Tests erfolgreich bestanden wurden.

## 6 GitHub Upload

Abschließend wurde das Projekt lokal geklont und sollte in ein eigenes Repository geladen werden. Beim ersten Versuch verweigerte GitHub den Zugriff mit dem Fehler 403, da die Berechtigung für das ursprüngliche Repository fehlte. Die Lösung bestand darin, ein neues leeres Repository im eigenen Account zu erstellen und die Remote Adresse im Terminal mit dem Befehl git remote set url origin entsprechend zu ändern. Danach war der Upload erfolgreich.

## 7 Beantwortung der Fragestellungen

**Was sind die Hauptmerkmale von JSON?**
JSON steht für JavaScript Object Notation und ist ein kompaktes Datenformat. Es besteht aus Schlüssel Wert Paaren und unterstützt einfache Datentypen wie Zeichenketten, Zahlen, Wahrheitswerte, Listen und Objekte. Die Struktur ist für Menschen leicht lesbar und für Maschinen einfach zu verarbeiten. Es ist sprachunabhängig und wird von nahezu allen modernen Programmiersprachen direkt unterstützt.

**Was sind die Hauptmerkmale von XML?**
XML steht für Extensible Markup Language und ist eine Auszeichnungssprache. Die Daten werden in einer hierarchischen Baumstruktur mit öffnenden und schließenden Tags abgebildet. Die Syntax ist sehr streng und erfordert eine wohlgeformte Struktur. XML ist extrem erweiterbar und unterstützt Namensräume sowie die Validierung gegen Schemata, was es besonders für komplexe und strenge Datenaustauschprozesse geeignet macht.

**Wie unterscheidet die Applikation in diesem Beispiel ob die Rückgabewerte in JSON oder XML zurückgegeben werden?**
Die Unterscheidung erfolgt über den HTTP Accept Header, welchen der Client bei seiner Anfrage mitsendet. Das Spring Boot Framework nutzt hierfür die Content Negotiation. Sendet der Client den Header mit dem Wert für JSON, wird die Antwort als JSON formatiert. Wird der Header für XML gesendet, erfolgt die Ausgabe als XML. Fehlt der Header komplett, wird standardmäßig JSON zurückgegeben.

**Was zeichnet ein REST Interface aus und warum wird es heute sehr oft als standardisierte Schnittstelle verwendet?**
Ein REST Interface nutzt die Standard HTTP Methoden wie GET, POST, PUT und DELETE zur Kommunikation. Es ist zustandslos, was bedeutet, dass der Server keine Sitzungsdaten zwischen den Anfragen speichert. Jede Anfrage muss alle nötigen Informationen enthalten. Zudem ist es ressourcenorientiert, da jede URL eine spezifische Ressource repräsentiert. Es wird heute oft verwendet, weil es einfach zu verstehen, plattformunabhängig und hervorragend skalierbar ist.

**Was ist der Unterschied zwischen den HTTP Methoden GET, POST, PUT und PATCH?**
GET wird verwendet, um Daten vom Server abzurufen, ohne diese zu verändern. POST wird genutzt, um eine neue Ressource auf dem Server zu erstellen. PUT dient dazu, eine vorhandene Ressource vollständig zu ersetzen oder zu aktualisieren. PATCH hingegen wird verwendet, um nur bestimmte Teile einer Ressource zu aktualisieren, ohne die restlichen Daten zu überschreiben.

**Ich will eine neue Request Methode oder einen neuen Pfad in meinem REST Interface zur Verfügung stellen. Welche Stellen des Quellcodes muss ich verändern?**
Zuerst muss die Controller Klasse angepasst werden, indem eine neue Methode mit der entsprechenden Annotation wie GetMapping oder PostMapping angelegt wird. Wenn für diese neue Route spezielle Geschäftslogik benötigt wird, muss zudem der Service Layer erweitert werden. Falls neue Datenstrukturen gespeichert oder abgerufen werden sollen, ist auch eine Anpassung des Repository notwendig.

**Kann man OpenAPI und Swagger vergleichen? Wenn ja dann vergleichen Sie beide Themengebiete. Wenn nein dann begründen die Antwort.**
Man kann sie vergleichen, da sie sehr eng miteinander verbunden sind. OpenAPI ist eine Spezifikation, also ein Standardformat zur Beschreibung von REST Schnittstellen. Swagger hingegen ist eine Sammlung von Werkzeugen, die diese OpenAPI Spezifikation nutzen. Swagger UI ist das grafische Werkzeug, welches die OpenAPI Beschreibung visualisiert und testbar macht. OpenAPI ist der Bauplan und Swagger ist das Werkzeug zur Anzeige.

**Wo liegt im folgenden Code Snippet der Fehler? Begründen die Antwort.**

```java
@GetMapping("/stations/add")
public String createStation(@RequestParam String name, @RequestParam double warning) {
    stationService.save(new Station(name, warning));
    return "Station successfully created!";
}
```

Der Fehler liegt in der Verwendung der HTTP Methode GetMapping. Diese Methode ist ausschließlich dazu gedacht, Daten abzurufen und darf den Zustand des Servers nicht verändern. Im Code Snippet wird jedoch eine neue Station über die Methode save gespeichert, was einer Datenänderung entspricht. Für das Erstellen neuer Daten muss zwingend die HTTP Methode PostMapping verwendet werden. Zudem ist es unsauber, bei einer Änderung einen einfachen Text zurückzugeben, anstatt einer ordentlichen HTTP Antwort mit dem Statuscode 201 Created.
