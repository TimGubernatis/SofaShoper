# 🛋️ Sofa-Shopper

**Get Your Stuff Now – direkt vom Sofa aus.**  
Moderne Shopping-App mit Fokus auf Einfachheit, Übersicht & Funktion.

---

## 📚 Inhaltsverzeichnis

1. [📝 Projektbeschreibung](#-projektbeschreibung)  
2. [🎨 App-Design](#-app-design)  
   - [🖼️ Weitere Screens](#weitere-screens)  
3. [✅ Features](#-features)  
4. [🛠️ Technischer Aufbau](#-technischer-aufbau)  
   - [🔧 Architektur & Struktur](#architektur--struktur)  
   - [💾 Datenmanagement](#datenmanagement)  
   - [🌐 API](#api)  
   - [📦 Verwendete Libraries](#verwendete-libraries)  
5. [🛒 Einkaufslogik & Bestand](#-einkaufslogik--bestand)  
6. [🔮 Ausblick](#-ausblick)
7. [👤 Kontakt](#-kontakt)

---

## 📝 Projektbeschreibung

**Sofa-Shopper** ist eine intuitive E-Commerce-App, die dir das Einkaufen so bequem wie möglich macht – direkt vom Sofa aus.  
Ob Fashion, Gadgets oder Lifestyle – die App bietet eine moderne Einkaufserfahrung mit Fokus auf:

- 🧭 **Übersichtlichkeit statt Überfrachtung**  
- ⚡ **Schneller Checkout**  
- 🎯 **Klares Design & direkte Bedienung**

> **Ziel**: Eine einfache, performante und moderne Shopping-App für alle, die keine Lust auf überladene Menüs und komplizierte Prozesse haben.

---

## 🎨 App-Design

| Startseite | Produktansicht | Warenkorb |
|------------|----------------|-----------|
| ![MainScreen](img/MainScreen.png) | ![DetailScreen](img/DetailScreen.png) | ![Warenkorb](img/Warenkorb.png) |

---

### Weitere Screens

| Favoriten | Zur Kasse | Bestellungen |
|-----------|-----------|--------------|
| ![Favoriten](img/Favoriten.png) | ![ZurKasse](img/ZurKasse.png) | ![Bestellungen](img/Bestellungen.png) |

---

## ✅ Features

- 🔍 Produktsuche mit Filteroptionen  
- 🛍️ Detaillierte Produktansicht mit Beschreibung & Bildern  
- 🧮 Warenkorb mit Live-Preisberechnung  
- ❤️ Favoriten-Funktion  
- 👤 Benutzerprofil mit Bestellübersicht  
- 🔐 Google-Login & Firebase Authentication  
- 🔔 Push-Benachrichtigungen  
- 🏷️ Adress- & Bestellverwaltung  
- 🧩 Fehlerbehandlung mit User-Feedback  
- 🌙 Dark Mode

---

## 🛠️ Technischer Aufbau

### 🔧 Architektur & Struktur

Das Projekt ist nach dem **MVVM-Pattern** aufgebaut und modular strukturiert:

```
/ui          → alle Screens & ausgelagerte Komponenten
/viewmodel   → zustandsbasierte Steuerung per StateFlow
/repository  → Vermittlung zwischen UI und Datenquelle (Repository Pattern)
/data        → Modellklassen, API-Services, Firebase-Integration
/di          → Dependency Injection (Koin)
```

> Diese Struktur ermöglicht eine saubere Trennung der Logik, einfache Wartung und gute Testbarkeit.

---

### 💾 Datenmanagement

- 🔐 **Firebase Authentication** – Login mit Google  
- ☁️ **Firebase Firestore** – Speicherung von Nutzer-, Adress- und Bestelldaten  
- 🗂️ **In-App-Speicherung** – temporärer Cache für Warenkorb & Favoriten

---

### 🌐 API

Die Produktdaten werden über die [Fake Store API](https://fakeapi.platzi.com) geladen – eine realistische Demo-Schnittstelle für E-Commerce-Projekte mit echten Produktdaten (inkl. Bildern, Preisen & Beschreibung).

---

### 📦 Verwendete Libraries

- 🔗 [Retrofit](https://square.github.io/retrofit/) – HTTP-Client für API-Zugriffe  
- 🧠 [Koin](https://insert-koin.io/) – Dependency Injection Framework  
- 🖼️ [Coil](https://coil-kt.github.io/coil/) – modernes Bild-Loading  
- 🧩 [Jetpack Compose](https://developer.android.com/jetpack/compose) – modernes UI-Toolkit  
- 🔥 [Firebase](https://firebase.google.com/) – Auth & Cloud Firestore

---

## 🛒 Einkaufslogik & Bestand

**Realistische Kauf- & Lagerlogik:**

- 📦 **Dynamischer Produktbestand**: Jeder Artikel hat Lagerbestand  
- 🛒 **Kauf-Simulation**: Lagerbestand verringert sich nach Kauf  
- 🚫 **„Ausverkauft“-Status**: Produkte mit Bestand = 0 können nicht gekauft werden  
- ⚠️ **Verfügbarkeits-Hinweis**: Anzeige „Nur noch X verfügbar!“  
- 🔄 **Warenkorbprüfung**: Menge darf Lagerbestand nicht überschreiten

> Diese Logik bietet realistische Simulationen – ideal für Prototypen & Usability-Tests.

---

## 🔮 Ausblick

Geplante Erweiterungen:

- [ ] 📦 Paket-Tracking & Sendungsverfolgung  
- [ ] 📝 Wunschlisten & Merkzettel  
- [ ] 🔍 Erweiterte Filter- & Sortierfunktionen  
- [ ] 🌐 Mehrsprachigkeit (Deutsch / Englisch)  
- [ ] 🎨 Weitere Personalisierungsoptionen  
- [ ] 🧱 Migration auf Supabase (statt Firebase)  
- [ ] 🖥️ Web-Interface zur Produktpflege

---

## 👤 Kontakt

> **Autor:** Tim Walter Gubernatis  
> 📧 Mail: [Guberto@kabelmail.de](mailto:Guberto@kabelmail.de)  
> 🔗 LinkedIn: [linkedin.com/in/tim-walter-gubernatis-00391534b](https://www.linkedin.com/in/tim-walter-gubernatis-00391534b)

---

**🧵 Made with ❤️ in Kotlin & Jetpack Compose**
