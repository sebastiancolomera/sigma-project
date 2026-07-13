# SIGMA — Gestor de Tareas para Equipos de Programación

> Proyecto universitario para el curso **Programación Orientada a Objetos (ICC490-1)**
> Universidad de La Frontera

---

## Descripción

**SIGMA** es una aplicación de escritorio desarrollada en Java que permite gestionar tareas dentro de equipos de programación. El sistema organiza el trabajo a través de **metas** (agrupaciones de tareas) y controla el acceso mediante tres roles de usuario distintos, cada uno con funcionalidades específicas.

---

## Integrantes

| Nombre | Rol en el equipo |
|---|---|
| Sebastián Colomera | Desarrollador |
| Sebastián Durán | Desarrollador |
| Felipe Rain | Desarrollador |

---

## Tecnologías utilizadas

- **Java 17**
- **Java Swing** — interfaz gráfica de escritorio
- **Maven** — gestión de dependencias y compilación
- **Gson 2.10.1** — serialización y deserialización JSON
- **JUnit 5 (Jupiter 5.10.1)** — pruebas unitarias

---

## Estructura del proyecto

```
sigma-project/
├── pom.xml                         # Configuración Maven (Gson, JUnit 5, Java 17)
├── .gitignore                      # Ignora .idea/, target/, data/*.json
├── README.md
├── data/
│   ├── .gitkeep                    # Mantiene data/ en git aunque los .json estén ignorados
│   ├── usuarios.json               # Persistencia: lista de usuarios (no versionado)
│   └── metas.json                  # Persistencia: metas y tareas (no versionado)
├── src/
│   ├── main/java/sigma/
│   │   ├── app/                     # Capa controlador / lógica de negocio
│   │   │   ├── Main.java                # Punto de entrada de la aplicación
│   │   │   ├── GestorSigma.java         # Fachada principal (controlador central)
│   │   │   ├── SigmaConfig.java         # Constantes (rutas, admin)
│   │   │   ├── ResultadoOperacion.java  # DTO para resultados con mensaje
│   │   │   ├── SeguridadUtil.java       # Hashing PBKDF2
│   │   │   ├── ServicioUsuarios.java    # Lógica CRUD de usuarios
│   │   │   ├── ServicioMetas.java       # Lógica CRUD de metas y tareas
│   │   │   └── ValidadorFecha.java      # Validaciones de fecha
│   │   ├── modelo/                  # Capa modelo (POJOs)
│   │   │   ├── Usuario.java
│   │   │   ├── Meta.java                # Contiene una lista de Tarea
│   │   │   ├── Tarea.java
│   │   │   ├── RolUsuario.java          # Enum: SUPERUSUARIO, LIDER, USUARIO
│   │   │   ├── EstadoTarea.java         # Enum: PENDIENTE, EN_PROCESO, COMPLETADA
│   │   │   └── EstadoEntrega.java       # Enum: EN_PLAZO, ENTREGADA, ENTREGADA_FUERA_DE_PLAZO,
|   |   |                                         FUERA_DE_PLAZO, POSTERGADA
│   │   ├── persistencia/
│   │   │   └── GestorJSON.java          # Serialización/deserialización JSON con Gson
│   │   └── vista/gui/               # Capa vista (Swing)
│   │       ├── LoginFrame.java
│   │       ├── MenuSuperusuarioFrame.java
│   │       ├── MenuLiderFrame.java
│   │       ├── MenuUsuarioFrame.java
│   │       ├── CambiarEstadoPanel.java
│   │       ├── GestionTareasPanel.java
│   │       ├── EditarFechasPanel.java
│   │       ├── ProgresoMetasPanel.java
│   │       ├── VerTareasDialog.java
│   │       └── VerUsuariosDialog.java
│   └── test/java/sigma/
│       ├── app/
│       │   ├── GestorSigmaTest.java
│       │   ├── ServicioUsuariosTest.java
│       │   ├── ServicioMetasTest.java
│       │   └── SeguridadUtilTest.java
│       ├── modelo/
│       │   ├── MetaTest.java
│       │   ├── TareaTest.java
│       │   └── UsuarioTest.java
│       └── persistencia/
│           └── GestorJSONTest.java
```

---

## Arquitectura

El proyecto sigue una arquitectura de **3 capas**, con una fachada como punto único de entrada al controlador:

```
VISTA (sigma.vista.gui)
    └── usa ↓
CONTROLADOR (sigma.app.GestorSigma)
    ├── ServicioUsuarios ──┐
    └── ServicioMetas ─────┤
                           └── usa ↓
PERSISTENCIA (sigma.persistencia.GestorJSON)
    └── lee/escribe ↓
data/*.json

sigma.modelo ← usado por todas las capas
```

`GestorSigma` actúa como controlador y delega en `ServicioUsuarios` y `ServicioMetas`.

---

## Roles de usuario

| Rol | Permisos |
|---|---|
| `SUPERUSUARIO` | Crear, editar y eliminar usuarios; resetear el sistema |
| `LIDER` | Crear metas, asignar tareas, editar fechas, cambiar estados (solo de tareas propias) y ver el progreso del equipo |
| `USUARIO` | Ver sus tareas asignadas y cambiar el estado de las mismas |

---

## Estados de una tarea

Cada tarea maneja dos estados independientes:

- **Estado de la tarea** (`EstadoTarea`): `PENDIENTE`, `EN_PROCESO`, `COMPLETADA`.
- **Estado de entrega** (`EstadoEntrega`): `EN_PLAZO`, `ENTREGADA`, `ENTREGADA_FUERA_DE_PLAZO`, `FUERA_DE_PLAZO`, `POSTERGADA`. Se recalcula automáticamente según la fecha actual y las fechas de inicio/término de la tarea.

---

## Requisitos previos

- **JDK 17** o superior instalado
- **Apache Maven 3.6+** instalado
- IDE recomendado: IntelliJ IDEA

---

## Instalación y ejecución

**1. Clonar el repositorio**

```bash
git clone https://github.com/<usuario>/sigma-project.git
cd sigma-project
```

**2. Compilar el proyecto**

```bash
mvn compile
```

**3. Ejecutar la aplicación**

```bash
mvn exec:java -Dexec.mainClass="sigma.app.Main"
```

O bien, ejecutar directamente `Main.java` desde el IDE.

---

## Credenciales por defecto

Al iniciar por primera vez con el sistema vacío, se crea automáticamente un usuario administrador:

| Usuario | Rol |
|---|---|
| `admin` | SUPERUSUARIO |

La contraseña se define mediante una ventana de registro al iniciar el programa (o cuando el Superusuario al resetear el sistema).

---

## Pruebas

Para ejecutar el conjunto de pruebas unitarias:

```bash
mvn test
```

El proyecto cuenta con 8 clases de test y más de 90 pruebas individuales, que cubren `GestorSigma`, `ServicioUsuarios`, `ServicioMetas`, `Meta`, `Tarea`, `Usuario` y `GestorJSON`, incluyendo casos borde (nombres vacíos, fechas inválidas, restricciones de permisos, etc.). Se usa `@TempDir` para aislar las pruebas de persistencia del sistema de archivos real.

---

## Persistencia de datos

Los datos se almacenan en archivos JSON dentro del directorio `data/`, usando Gson con adaptadores personalizados para `LocalDate`, `RolUsuario`, `EstadoTarea` y `EstadoEntrega`:

- `data/usuarios.json` — lista de usuarios registrados
- `data/metas.json` — metas y sus tareas asociadas

> **Nota:** estos archivos están excluidos del repositorio por `.gitignore`. El directorio `data/` se mantiene rastreado mediante el archivo `.gitkeep`.

---

## Licencia

Proyecto académico sin licencia comercial.
Uso exclusivo para evaluación en el curso ICC490-1 — UFRO.
