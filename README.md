# vaadinApps
WebUI to control Mirror and Heating-Control with vaadin

Das Projekt realisiert die Web-GUI für webgesteuerte Spiegel, die Speicherung der Wärme und die daran angeschlossene Heizungssteuerung/Regelung. 

Den ganzen Tag über wird die Sonne von den Spiegeln auf einen Wärmekollektor gelenkt. Damit sammle ich konzentriert Wärme, die in einem Speicher gehalten wird.

Aufgaben der Web-GUI für PC und Handy (dafür benutze ich VAADIN):

- Einstellung des Zielpunktes, 
- Positionsvorgabe zu verschiedenen Uhrzeiten,
- Geographische Position per GoogleMap (Sonnenformel!)
- Zeitprogramm (Wann soll welches Ziel angeleuchtet werden)
- Einstellung Sturmabschaltung (per Webservice Windstärke erfragen, aus dem Wind drehen bei Sturm)
- Heizungsreglerparameter
- Darstellung der Temperaturkurven von Solaranlage und Heizung per Charts. 


Der Spiegel-Controller ist übrigens momentan ein Rasp-Pi mit Wlanstick, Kompass/Lagesensor und Camera-Modul. Siehe Projekt MirrorControl.
