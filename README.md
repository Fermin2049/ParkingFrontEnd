# 🚗 ParkingFrontEnd

**ParkingFrontEnd** es una aplicación móvil desarrollada en **Android con Java y MVVM**, diseñada para **gestionar reservas de estacionamiento, pagos y autenticación de usuarios**. Se conecta a la **API REST de ParkingProject**, proporcionando una experiencia intuitiva y segura para la administración de estacionamientos en tiempo real.

---

## 📑 Tabla de Contenidos

1. [Introducción](#-introducción)
2. [Características](#-características)
3. [Arquitectura](#%EF%B8%8F-arquitectura)
4. [Requisitos](#%EF%B8%8F-requisitos)
5. [Instalación](#%EF%B8%8F-instalación)
6. [Estructura del Proyecto](#-estructura-del-proyecto)
7. [Uso](#-uso)
8. [Contribuciones](#%F0%9F%A4%9D-contribuciones)
9. [Licencia](#-licencia)

---

## 📌 Introducción

**ParkingFrontEnd** es la aplicación móvil complementaria del backend **ParkingProject**, permitiendo a los usuarios:

- **Reservar espacios de estacionamiento** en tiempo real.
- **Realizar y gestionar pagos** de manera segura.
- **Autenticarse** mediante credenciales o huella digital.

La aplicación se comunica con una **API REST** desarrollada en **ASP.NET Core**, implementando una **arquitectura MVVM** para modularidad y escalabilidad.

---

## 🚀 Características

✅ **Gestión de Reservas**: Buscar, seleccionar y reservar espacios de estacionamiento.  
✅ **Procesamiento de Pagos**: Integración con sistemas de pago y validación de transacciones.  
✅ **Autenticación Segura**: Inicio de sesión con **JWT** o **huella digital**.  
✅ **Interfaz Moderna**: Diseño optimizado con **Material Design**.  
✅ **Conexión a API REST**: Integración con el backend **ParkingProject** para sincronización en tiempo real.  

---

## 🏗️ Arquitectura

El proyecto sigue la **arquitectura MVVM**, separando la **lógica de negocio** de la **interfaz de usuario**:

- **Modelo (Model)**: Representa las entidades del sistema:
  - `Reserva.java` → Datos de la reserva de estacionamiento.
  - `Pago.java` → Registro de pagos.
  - `Cliente.java` → Información del usuario autenticado.
- **Vista (View)**: Compuesta por **Fragments y Activities en XML**, con navegación fluida entre pantallas.
- **ViewModel**: Maneja la lógica de negocio y comunicación con la API:
  - `LoginViewModel.java`
  - `ReservarViewModel.java`
  - `PaymentViewModel.java`
- **Conexión a API REST**: Implementada con **Retrofit** en:
  - `ApiService.java` → Definición de endpoints.
  - `ApiClient.java` → Configuración del cliente de red.

---

## ⚙️ Requisitos

Antes de ejecutar este proyecto, asegúrate de tener instalado:

- **Android Studio** (última versión recomendada).
- **JDK 11** o superior.
- **Emulador Android** o **dispositivo físico**.
- **Backend de ParkingProject** en ejecución.

---

## 📥 Instalación

### 1️⃣ Clonar este repositorio

```sh
git clone https://github.com/Fermin2049/ParkingFrontEnd.git
cd ParkingFrontEnd
```

### 2️⃣ Abrir en Android Studio

- Importar el proyecto en **Android Studio**.
- Asegurar que el **SDK de Android** está correctamente configurado.

### 3️⃣ Configurar la API REST

- Editar `ApiClient.java` y reemplazar la URL con la del backend **ParkingProject**.

### 4️⃣ Ejecutar la aplicación

- Conectar un **dispositivo físico** o usar un **emulador**.
- Presionar **Run** en **Android Studio**.

---

## 📂 Estructura del Proyecto

```bash
ParkingFrontEnd/
│── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml             # Configuración de permisos y actividades
│   │   ├── java/com/fermin2049/parking/
│   │   │   ├── data/models/                # Modelos de datos
│   │   │   │   ├── Reserva.java
│   │   │   │   ├── Pago.java
│   │   │   │   ├── Cliente.java
│   │   │   ├── iu/auth/                    # Módulo de autenticación
│   │   │   │   ├── LoginFragment.java
│   │   │   │   ├── LoginViewModel.java
│   │   │   │   ├── RegisterFragment.java
│   │   │   │   ├── RegisterViewModel.java
│   │   │   ├── iu/dashboard/               # Panel de control del usuario
│   │   │   │   ├── DashboardFragment.java
│   │   │   │   ├── DashboardViewModel.java
│   │   │   ├── iu/payment/                 # Gestión de pagos
│   │   │   │   ├── PaymentFragment.java
│   │   │   │   ├── PaymentViewModel.java
│   │   │   ├── iu/reservations/            # Reservas de estacionamiento
│   │   │   │   ├── ReservarFragment.java
│   │   │   │   ├── ReservarViewModel.java
│   │   │   ├── network/                    # Conexión con API REST
│   │   │   │   ├── ApiClient.java
│   │   │   │   ├── ApiService.java
│   │   ├── res/layout/
│   │   │   ├── activity_main.xml           # UI principal
│   │   │   ├── fragment_dashboard.xml      # UI de dashboard
│   │   │   ├── fragment_login.xml          # UI de login
│   │   │   ├── fragment_reservar.xml       # UI de reservas
│   │   │   ├── fragment_payment.xml        # UI de pagos
│   ├── build.gradle.kts                     # Configuración de dependencias
│── settings.gradle.kts                      # Configuración del proyecto
```

---

## 📌 Uso

### 🔹 Funcionalidades principales

#### 🏠 Reservas
- Buscar y **reservar estacionamientos** disponibles.
- Consultar **historial de reservas**.

#### 💰 Pagos
- **Realizar pagos** y verificar transacciones.
- **Ver historial de facturación**.

#### 🔐 Autenticación
- Inicio de sesión con **credenciales** o **huella digital**.
- Registro y recuperación de cuenta.

Para mayor detalle, revisa los archivos en la carpeta `iu/`.

---

## 🤝 Contribuciones

¡Las contribuciones son bienvenidas! Sigue estos pasos:

1️⃣ **Haz un fork** del repositorio.  
2️⃣ **Crea una nueva rama**:  
```sh
git checkout -b feature/nueva-funcionalidad
```
3️⃣ **Realiza tus cambios y haz un commit**:  
```sh
git commit -m "Agregar nueva funcionalidad"
```
4️⃣ **Sube los cambios**:  
```sh
git push origin feature/nueva-funcionalidad
```
5️⃣ **Abre un Pull Request**.

---

![image](https://github.com/user-attachments/assets/44fb63f2-96a9-4afa-9322-b2f23218e966)
![image](https://github.com/user-attachments/assets/0565e06c-49fe-419e-b2d2-3a67415d7542)
![image](https://github.com/user-attachments/assets/abb53d32-9cef-4665-b983-322b69628b47)
![image](https://github.com/user-attachments/assets/acd76db5-99bb-4215-8841-00a78b370f08)





## 📜 Licencia

Este proyecto está licenciado bajo la **Licencia MIT**.
