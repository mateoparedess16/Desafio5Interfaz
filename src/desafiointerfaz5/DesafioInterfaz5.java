/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package desafiointerfaz5;

import java.util.Random;

public class DesafioInterfaz5 {

    public static void main(String[] args) {
        System.out.println("🌿 ECOSISTEMA VIRTUAL – SELVA 🌿\n");

        Jaguar jaguar = new Jaguar("T'Chal", 90, 65, 80);
        Mono mono     = new Mono("Pumpi", 45, 25, 35);
        Loro loro     = new Loro("Kiko",  12, 10, 60);
        Serpiente boa = new Serpiente("Naga", 20, 18, 15);

        // Estado inicial
        mostrarEstado(jaguar, mono, loro, boa);

        // Turno 1
        separador("TURNO 1");
        mono.beberAgua();         // +energía
        loro.volar();             // gasta energía
        boa.acechar();            // gasta poco
        jaguar.refugiarse();      // se oculta en la vegetación

        // Turno 2
        separador("TURNO 2");
        jaguar.cazar(mono);       // caza al mono (si puede)
        mono.treparArbol();       // intenta ponerse a salvo
        boa.morder(loro);         // muerde al loro
        loro.refugiarse();        // busca protección en la vegetación

        // Turno 3
        separador("TURNO 3");
        mono.comer();             // fruta = recupera energía
        jaguar.comer();           // si cazó antes, repone energía; si no, busca
        loro.emitirSonido();      // alerta
        boa.refugiarse();         // se enrosca y se esconde

        // Estado final
        separador("ESTADO FINAL");
        mostrarEstado(jaguar, mono, loro, boa);
        System.out.println("\n✅ Fin de la simulación.");
    }

    private static void mostrarEstado(Animal... animales) {
        for (Animal a : animales) {
            System.out.println(a);
        }
        System.out.println();
    }

    private static void separador(String titulo) {
        System.out.println("\n========== " + titulo + " ==========\n");
    }
}

/* ============================================================
   INTERFACE DEL HÁBITAT: SELVA
   Acciones propias del entorno que todos los animales deben
   saber realizar a su manera (polimorfismo).
   ============================================================ */
interface HabitatSelva {
    void refugiarse();        // usar vegetación/terreno para cubrirse
    void beberAgua();         // recuperar energía bebiendo del río
    void interactuarConPlantas(); // conducta con la flora (lianas, semillas, etc.)
}

/* ============================================================
   CLASE ABSTRACTA MADRE: ANIMAL
   - Encapsula atributos
   - Validaciones en setters
   - Control de energía
   - Métodos abstractos para polimorfismo
   ============================================================ */
abstract class Animal {
    private String nombre;
    private double pesoKg;   // validado > 0
    private int energia;     // 0..100
    private int velocidad;   // km/h >= 0

    private static final int ENERGIA_MAX = 100;

    protected final Random rnd = new Random();

    public Animal(String nombre, double pesoKg, int energia, int velocidad) {
        setNombre(nombre);
        setPesoKg(pesoKg);
        setEnergia(energia);
        setVelocidad(velocidad);
    }

    // ====== comportamiento general ======
    protected boolean gastarEnergia(int costo) {
        if (energia < costo) {
            System.out.println(nombre + " está cansado/a (energía insuficiente: " + energia + ").");
            return false;
        }
        energia -= costo;
        return true;
    }

    protected void recuperarEnergia(int cant) {
        energia = Math.min(ENERGIA_MAX, energia + Math.max(0, cant));
    }

    protected void perderEnergia(int cant) {
        energia = Math.max(0, energia - Math.max(0, cant));
    }

    public boolean puedeActuar() {
        return energia > 0;
    }

    // ====== abstractos (polimorfismo) ======
    public abstract void emitirSonido();
    public abstract void mover();
    public abstract void comer();

    // ====== getters/setters (encapsulación + validación) ======
    public String getNombre() { return nombre; }
    public double getPesoKg() { return pesoKg; }
    public int getEnergia() { return energia; }
    public int getVelocidad() { return velocidad; }

    public final void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) nombre = "SinNombre";
        this.nombre = nombre.trim();
    }

    public final void setPesoKg(double pesoKg) {
        if (pesoKg <= 0) throw new IllegalArgumentException("pesoKg debe ser > 0");
        this.pesoKg = pesoKg;
    }

    public final void setEnergia(int energia) {
        if (energia < 0) energia = 0;
        if (energia > ENERGIA_MAX) energia = ENERGIA_MAX;
        this.energia = energia;
    }

    public final void setVelocidad(int velocidad) {
        if (velocidad < 0) velocidad = 0;
        this.velocidad = velocidad;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
               " {nombre='" + nombre + "', pesoKg=" + pesoKg +
               ", energia=" + energia + ", velocidad=" + velocidad + "}";
    }
}

/* ============================================================
   ANIMALES DE LA SELVA (3-4 clases)
   Todos: extienden Animal e implementan HabitatSelva
   ============================================================ */

class Jaguar extends Animal implements HabitatSelva {
    private boolean tienePresa;

    public Jaguar(String nombre, double pesoKg, int energia, int velocidad) {
        super(nombre, pesoKg, energia, velocidad);
    }

    @Override public void emitirSonido() {
        if (!puedeActuar()) return;
        System.out.println(getNombre() + " ruge con fuerza.");
        gastarEnergia(3);
    }

    @Override public void mover() {
        if (!gastarEnergia(6)) return;
        System.out.println(getNombre() + " avanza sigiloso entre la maleza.");
    }

    @Override public void comer() {
        if (tienePresa) {
            System.out.println(getNombre() + " devora su presa y recupera energía.");
            recuperarEnergia(25);
            tienePresa = false;
        } else {
            System.out.println(getNombre() + " no tiene presa. Descansa para ahorrar energía.");
            recuperarEnergia(8);
        }
    }

    // Propia
    public void cazar(Animal presa) {
        if (!gastarEnergia(20)) return;

        System.out.println(getNombre() + " intenta cazar a " + presa.getNombre() + "...");
        // Éxito si velocidad del jaguar supera a la presa o sale “suerte”
        boolean exito = getVelocidad() > presa.getVelocidad() || rnd.nextDouble() < 0.35;

        if (exito && presa.puedeActuar()) {
            System.out.println("¡" + getNombre() + " atrapa a " + presa.getNombre() + "!");
            presa.perderEnergia(35);
            tienePresa = true;
        } else {
            System.out.println(presa.getNombre() + " escapa por poco.");
            perderEnergia(4); // desgaste por persecución fallida
        }
    }

    // ===== HabitatSelva =====
    @Override public void refugiarse() {
        if (!gastarEnergia(5)) return;
        System.out.println(getNombre() + " se oculta tras las lianas y hojas anchas.");
    }
    @Override public void beberAgua() {
        System.out.println(getNombre() + " bebe agua del río y se refresca.");
        recuperarEnergia(12);
    }
    @Override public void interactuarConPlantas() {
        System.out.println(getNombre() + " marca territorio cerca de un árbol caído.");
        gastarEnergia(2);
    }
}

class Mono extends Animal implements HabitatSelva {
    public Mono(String nombre, double pesoKg, int energia, int velocidad) {
        super(nombre, pesoKg, energia, velocidad);
    }

    @Override public void emitirSonido() {
        if (!puedeActuar()) return;
        System.out.println(getNombre() + " chilla para alertar a la manada.");
        gastarEnergia(2);
    }

    @Override public void mover() {
        if (!gastarEnergia(4)) return;
        System.out.println(getNombre() + " salta entre ramas rápidamente.");
    }

    @Override public void comer() {
        System.out.println(getNombre() + " come frutas dulces. (+energía)");
        recuperarEnergia(15);
    }

    public void treparArbol() {
        if (!gastarEnergia(8)) return;
        System.out.println(getNombre() + " trepa alto para evitar depredadores.");
    }

    // ===== HabitatSelva =====
    @Override public void refugiarse() {
        if (!gastarEnergia(5)) return;
        System.out.println(getNombre() + " se oculta entre hojas y ramas.");
    }
    @Override public void beberAgua() {
        System.out.println(getNombre() + " bebe agua cuidadosamente del río.");
        recuperarEnergia(10);
    }
    @Override public void interactuarConPlantas() {
        System.out.println(getNombre() + " recolecta frutos y semillas.");
        gastarEnergia(3);
    }
}

class Loro extends Animal implements HabitatSelva {
    public Loro(String nombre, double pesoKg, int energia, int velocidad) {
        super(nombre, pesoKg, energia, velocidad);
    }

    @Override public void emitirSonido() {
        if (!puedeActuar()) return;
        System.out.println(getNombre() + " grita: ¡Cuidado! ¡Cuidado!");
        gastarEnergia(2);
    }

    @Override public void mover() {
        volar();
    }

    @Override public void comer() {
        System.out.println(getNombre() + " picotea semillas. (+energía)");
        recuperarEnergia(8);
    }

    public void volar() {
        if (!gastarEnergia(6)) return;
        System.out.println(getNombre() + " se eleva y cambia de árbol.");
    }

    // ===== HabitatSelva =====
    @Override public void refugiarse() {
        if (!gastarEnergia(4)) return;
        System.out.println(getNombre() + " se esconde en un hueco del tronco.");
    }
    @Override public void beberAgua() {
        System.out.println(getNombre() + " bebe gotas de agua atrapadas en hojas.");
        recuperarEnergia(6);
    }
    @Override public void interactuarConPlantas() {
        System.out.println(getNombre() + " ayuda a dispersar semillas al comer.");
        gastarEnergia(2);
    }
}

class Serpiente extends Animal implements HabitatSelva {
    public Serpiente(String nombre, double pesoKg, int energia, int velocidad) {
        super(nombre, pesoKg, energia, velocidad);
    }

    @Override public void emitirSonido() {
        if (!puedeActuar()) return;
        System.out.println(getNombre() + " sisea suavemente.");
        gastarEnergia(1);
    }

    @Override public void mover() {
        if (!gastarEnergia(3)) return;
        System.out.println(getNombre() + " se desliza silenciosamente por el suelo.");
    }

    @Override public void comer() {
        System.out.println(getNombre() + " traga una presa pequeña y descansa.");
        recuperarEnergia(20);
    }

    public void acechar() {
        if (!gastarEnergia(2)) return;
        System.out.println(getNombre() + " permanece inmóvil acechando a su presa.");
    }

    public void morder(Animal presa) {
        if (!gastarEnergia(12)) return;
        System.out.println(getNombre() + " muerde a " + presa.getNombre() + " inyectando veneno.");
        presa.perderEnergia(18);
    }

    // ===== HabitatSelva =====
    @Override public void refugiarse() {
        if (!gastarEnergia(2)) return;
        System.out.println(getNombre() + " se oculta bajo hojas y raíces.");
    }
    @Override public void beberAgua() {
        System.out.println(getNombre() + " bebe en la orilla sin ser visto.");
        recuperarEnergia(8);
    }
    @Override public void interactuarConPlantas() {
        System.out.println(getNombre() + " utiliza la vegetación para camuflarse.");
        gastarEnergia(2);
    }
}

