# 🛋️ Sofa-Shopper

**Get Your Stuff Now.**

## 📝 Beschreibung

**Sofa-Shopper** ist deine smarte Shopping-App für entspannte Einkaufserlebnisse vom Sofa aus.  
Egal ob Mode, Technik oder Lifestyle-Produkte – mit wenigen Klicks findest du genau das, was du brauchst.

Die App richtet sich an alle, die schnell, bequem und effizient shoppen wollen – ohne sich durch komplizierte Menüs kämpfen zu müssen.

> Unser Ziel: **Intuitives Design, schlanker Bestellprozess, keine Ablenkung.**  
> Im Gegensatz zu anderen Shopping-Apps setzt Sofa-Shopper auf Übersichtlichkeit, einfache Bedienung und ein modernes Look-and-Feel.

---

## 🎨 Design

| Startseite | Produktansicht | Warenkorb |
|------------|----------------|-----------|
| ![Startseite](./img/screen1.png) | ![Produktansicht](./img/screen2.png) | ![Warenkorb](./img/screen3.png) |

---

## ✅ Features

- [x] Produktliste mit Such- und Filterfunktion  
- [x] Detaillierte Produktansicht mit Bildern und Beschreibung  
- [x] Warenkorb mit Live-Preisberechnung  
- [x] Favoritenfunktion für Produkte  
- [x] Benutzerprofil mit Bestellübersicht  
- [x] Dark Mode  

---

## 🛠️ Technischer Aufbau

### 🔧 Projektstruktur & Architektur

Das Projekt folgt dem **MVVM-Pattern** und ist modular aufgebaut:

```
/ui          → alle Screens & Composables
/viewmodel   → zustandsbasierte Steuerung per StateFlow
/repository  → Vermittlung zwischen UI und Datenquelle
/data        → Modellklassen & API-Anbindung
```

> Diese Struktur sorgt für klare Trennung von Verantwortlichkeiten und einfache Testbarkeit.

---

### 💾 Datenspeicherung

- Aktuell: **temporäre In-Memory-Datenhaltung**  
- Geplant: Integration mit **Room** (lokale Datenbank) für Offline-Nutzung  
- Optional: Cloud-Sync via **Firebase Firestore**

---

### 🌐 API

Die Produktdaten stammen von der **[Fake Store API](https://fakestoreapi.com)**  
> Diese API bietet eine realistische Testumgebung für E-Commerce-Apps mit Produkten, Bildern, Preisen & mehr.

---

### 📦 Drittanbieter-Frameworks

- [Retrofit](https://square.github.io/retrofit/) – für Netzwerkaufrufe  
- [Koin](https://insert-koin.io/) – Dependency Injection  
- [Coil](https://coil-kt.github.io/coil/) – Bild-Loading  
- [Jetpack Compose](https://developer.android.com/jetpack/compose) – UI-Toolkit  

---

## 🔮 Ausblick

- [ ] Benutzer-Login mit Firebase Auth  
- [ ] Wunschzettel & Merklisten  
- [ ] Push-Benachrichtigungen bei Angeboten  
- [ ] Bestellverfolgung mit Tracking  
- [ ] Mehrsprachigkeit (DE/EN)

---
