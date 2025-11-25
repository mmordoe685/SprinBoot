

#  **Proyecto Spring Boot — Plataforma de Gestión de Biblioteca**

##  **Descripción del Proyecto**

Este proyecto consiste en el desarrollo de una **plataforma de gestión de biblioteca** creada con **Spring Boot**, que permite administrar libros, autores, categorías, usuarios y préstamos.
El sistema implementa un **módulo de autenticación avanzado**, integración con **APIs externas**, panel de administración, dashboards personalizados y utilidades de exportación de datos.

La temática elegida es la **gestión bibliotecaria**, proporcionando tanto herramientas administrativas como funcionalidades públicas para usuarios invitados.

---

#  **Funcionalidades Implementadas**

###  **1. Sistema de Autenticación y Autorización**

* Login mediante **OAuth2** con Google y GitHub.
* Gestión de 3 roles:

  * **Administrador**: acceso total.
  * **Usuario Registrado**: puede gestionar sus préstamos, ver y editar su perfil.
  * **Invitado**: solo accede a información pública.
* Auditoría de acciones (qué usuario realiza cada operación).
* Visualización del usuario logueado en la barra de navegación.
* Restricciones de vistas y menú según el rol.

---

###  **2. Integración de APIs Externas**

#### 🔹 API Obligatoria — Email (SendGrid / MailJet)

Utilizada para:

* Envío de correo de bienvenida.
* Confirmación de cuenta.
* Recuperación de contraseña vía enlace único.
* Envío de resumen semanal de actividad.

#### 🔹 API Adicional #1 — Gestión de Usuarios

Usada para poder gestionar desde el panel de administración los usuarios registrados, 
tanto los normales como los admins.

#### 🔹 API Adicional #2 — Gestión de Datos



###  **3. Funcionalidades Adicionales**

####  3.1 Panel de Administración

Incluye:

* Métricas generales: usuarios activos, préstamos, libros registrados.
* Gráficas dinámicas con Chart.js.
* Gestión de usuarios (activar/desactivar).
* Historial de logs del sistema.

####  3.2 Sistema de Exportación

Exportación de datos filtrados por:

* Rango de fechas
* Estado
* Categorías

Formatos:

* **Excel/CSV** con todos los campos.

####  3.3 Búsqueda y Filtrado Avanzado

* Búsqueda por múltiples campos.
* Rango de fechas.
* Ordenación dinámica.

####  3.5 Dashboard Personalizado

Cada usuario cuenta con:

* Resumen de actividad.
* Edición de sus datos personales.
* Foto de usuario mediante **Gravatar**.

---

#  **Requisitos Técnicos Implementados**

* Arquitectura MVC con servicios, repositorios y controladores separados.
* Plantillas con **Thymeleaf + Bootstrap** (responsive).
* Cache de datos frecuentes.
* Configuración con OAuth2 + Security Filters.
* Logs persistentes de operaciones críticas.
* Paginación en todos los listados.
* Lazy loading en relaciones JPA.

---

#  **Instrucciones de Instalación y Configuración**

## 1️⃣ **Requisitos previos**

* Java 17
* Maven 3.8+
* MySQL 
* Claves de APIs externas 

---

## 2️⃣ **Configuración del archivo `application.properties`**

```properties
spring.application.name=libreria

# Parametros de conexion a la bd Mysql
spring.datasource.url=jdbc:mysql://localhost:3306/biblioteca
spring.datasource.username=root
spring.datasource.password=usuario

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.mvc.format.date=yyyy-MM-dd

server.port=8081

logging.level.root=INFO
logging.level.com.tuempresa.tuapp=DEBUG
logging.file.name=logs/aplicacion.log
logging.logback.rollingpolicy.max-file-size=10MB
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg

spring.mail.host=smtp.sendgrid.net
spring.mail.port=587
spring.mail.username=apikey
spring.mail.password=${SENDGRID_API_KEY}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

app.mail.from=recibodelibreria@gmail.com
app.mail.base-url=http://localhost:8080

spring.thymeleaf.cache=false

# ---------- GOOGLE ----------
spring.security.oauth2.client.registration.google.client-id=12227604409-dsb77c3t3ovq3rfgf4bb9f1bspr5ro6c.apps.googleusercontent.com
spring.security.oauth2.client.registration.google.client-secret=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
spring.security.oauth2.client.registration.google.scope=openid,profile,email

# ---------- GITHUB  ----------
spring.security.oauth2.client.registration.github.client-id=Ov23lieRRu0KC3OLmXKQ
spring.security.oauth2.client.registration.github.client-secret=7ef95f396e2e7002de0cbbb48ce2a7a1789e0572
spring.security.oauth2.client.registration.github.scope=user:email

```

---

## 3️⃣ **Ejecución del Proyecto**


La aplicación estará disponible en:

 **[http://localhost:8081](http://localhost:8081)**

---

#  **Credenciales de Prueba (todos los roles)**

###  **Administrador**

```
usuario: admin
password: 12345
```

###  **Usuario Registrado**

```
email: ejemplo@+++.++
password: ejemplo
```

###  **Invitado**

No necesita credenciales — acceso público.

---

#  **Capturas de Pantalla**


---
