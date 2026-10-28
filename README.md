# API del Servicio de Mensajería (Courier Service)

El **Courier-service** es un servicio backend desarrollado con Java y Spring Boot, diseñado para gestionar operaciones de entrega, disponibilidad de conductores y asignación de pedidos. Proporciona puntos de enlace (endpoints) RESTful para el seguimiento de conductores en tiempo real, validación de disponibilidad y mapeo del servicio.

## Tabla de Contenidos

* [Arquitectura y Stack Tecnológico](#arquitectura-y-stack-tecnológico)

* [Características Principal](#características-principales)

* [Estructura del Proyecto](#estructura-del-proyecto)

* [Prerrequisitos](#prerrequisitos)

* [Inicio Rápido](#inicio-rápido)

* [Soporte para Docker](#soporte-para-docker)

* [Endpoints de la API](#endpoints-de-la-api)

* [Pruebas (Testing)](#pruebas-testing)

## Arquitectura y Stack Tecnológico

Este proyecto sigue los principios de **Diseño Guiado por el Dominio (DDD)** y **Arquitectura Hexagonal / Por Capas** para separar la lógica de negocio de las dependencias externas.

* **Lenguaje:** Java 25

* **Framework:** Spring Boot 4.x (Spring Web, Spring Data JPA)

* **Herramienta de Construcción:** Maven

* **Base de Datos:** MySQL

* **Contenedores:** Docker & Docker Compose

* **Documentación y Mapeo:** MapStruct, Lombok, OpenAPI / Swagger

## Características Principales

* **Modelo de Dominio de Mensajería:** Gestión de entidades de dominio para repartidores, vehículos y seguimiento de estados.

* **Validación de Disponibilidad:** Endpoints REST para consultar y validar la disponibilidad de conductores activos para la recepción de pedidos.

* **Mapeadores DTO:** Capa de transformación eficiente mediante MapStruct entre entidades de dominio, entidades JPA y DTOs de solicitud/respuesta de la API.

* **Patrón Repositorio:** Abstracción de la capa de persistencia compatible con operaciones de datos relacionales.

* **Soporte para Docker:** Configuración totalmente contenedorizada para un desarrollo y despliegue fluidos.

## Estructura del Proyecto

```
courier-service/
├── src/
│   ├── main/
│   │   ├── java/com/courier/service/
│   │   │   ├── controller/      # Controladores REST API
│   │   │   ├── domain/          # Entidades de Dominio y Lógica de Negocio
│   │   │   ├── dto/             # Objetos de Transferencia de Datos (DTO)
│   │   │   ├── mapper/          # Interfaces Mapeadoras de MapStruct
│   │   │   ├── repository/      # Repositorios de Spring Data
│   │   │   └── service/         # Implementación de Lógica de Negocio
│   │   └── resources/
│   │       ├── application.yml  # Propiedades de Configuración
│   │       └── db/migration/    # Scripts y Migraciones de Base de Datos
│   └── test/                    # Pruebas Unitarias y de Integración
├── Dockerfile                   # Configuración de Imagen Docker
├── docker-compose.yml           # Configuración Multicontenedor (API + BD)
└── pom.xml / build.gradle       # Archivo de Definición de Construcción

```

## Prerrequisitos

Antes de ejecutar la aplicación, asegúrate de tener instalado lo siguiente:

* [Java Development Kit (JDK 25)](https://www.oracle.com/java/technologies/downloads/)

* [Maven](https://maven.apache.org/)

* [Docker](https://www.docker.com/) y Docker Compose

## Inicio Rápido

### Ejecución Local

1. **Clonar el repositorio:**

   ```
   git clone https://github.com/tu-organizacion/courier-service.git
   cd courier-service
   
   ```

2. **Compilar el proyecto:**

   ```
   
   ./mvnw clean package
   
   ```

3. **Ejecutar la aplicación:**

   ```
   
   ./mvnw spring-boot:run
   
   ```

## Soporte para Docker

Puedes construir y ejecutar la aplicación utilizando Docker y Docker Compose.

### Construir la Imagen Docker

```
docker build -t courier-service:latest .

```

### Ejecutar con Docker Compose

Inicia tanto el servicio API como la base de datos PostgreSQL:

```
docker-compose up -d

```

Para detener el entorno de contenedores:

```
docker-compose down

```

## Endpoints de la API

| Método     | Endpoint                                              | Descripción                                                       | 
|------------|-------------------------------------------------------|-------------------------------------------------------------------| 
| **GET**    | `/api/v1/repartidores`                                | Obtener todos los mensajeros/repartidores activos                 | 
| **GET**    | `/api/v1/repartidores/{dniRepartidor}`                | Obtener perfil detallado del repartidor por DNI                   | 
| **POST**   | `/api/v1/repartidores`                                | Registrar un nuevo repartidor                                     | 
| **GET**    | `/api/v1/repartidores/{dniRepartidor}/estaDisponible` | Validar disponibilidad de conductores para solicitudes de entrega | 
| **PUT**    | `/api/v1/repartidores/{dniRepartidor}`                | Actualizar la información registrada de un repartidor existente.  | 
| **DELETE** | `/api/v1/repartidores/{dniRepartidor}`                | Eliminar un repartidor en base a su DNI.                          |
| **PUT**    | `/api/v1/repartidores/{dniRepartidor}/estado`         | Gestionar el estado de un repartidor basandose en su DNI.         |

## Pruebas (Testing)

Ejecuta las pruebas unitarias y de integración con comprobación de cobertura usando:

```

./mvnw test

```