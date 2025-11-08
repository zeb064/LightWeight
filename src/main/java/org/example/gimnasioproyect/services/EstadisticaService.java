package org.example.gimnasioproyect.services;

import org.example.gimnasioproyect.Utilidades.CalculadoraFechas;
import org.example.gimnasioproyect.Utilidades.MembresiaData;
import org.example.gimnasioproyect.Utilidades.TopClienteData;
import org.example.gimnasioproyect.Utilidades.Validador;
import org.example.gimnasioproyect.model.*;
import org.example.gimnasioproyect.repository.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class EstadisticaService {
    private final AsistenciaRepository asistenciaRepository;
    private final MembresiaClienteRepository membresiaClienteRepository;
    private final AsignacionEntrenadorRepository asignacionEntrenadorRepository;
    private final ClienteRepository clienteRepository;
    private final MembresiaRepository membresiaRepository;
    private final RutinaAsignadaRepository rutinaAsignadaRepository;

    public EstadisticaService(AsistenciaRepository asistenciaRepository,
                              MembresiaClienteRepository membresiaClienteRepository,
                              AsignacionEntrenadorRepository asignacionEntrenadorRepository,
                              ClienteRepository clienteRepository,
                              MembresiaRepository membresiaRepository,
                              RutinaAsignadaRepository rutinaAsignadaRepository) {
        this.asistenciaRepository = asistenciaRepository;
        this.membresiaClienteRepository = membresiaClienteRepository;
        this.asignacionEntrenadorRepository = asignacionEntrenadorRepository;
        this.clienteRepository = clienteRepository;
        this.membresiaRepository = membresiaRepository;
        this.rutinaAsignadaRepository = rutinaAsignadaRepository;
    }
    // ==================== ESTADÍSTICAS DE ASISTENCIA ====================

    // Cuenta el total de asistencias de un cliente
    public int contarAsistenciasCliente(String documentoCliente) throws SQLException {
        Validador.validarDocumento(documentoCliente);
        return asistenciaRepository.countAsistenciasByCliente(documentoCliente);
    }

    // Cuenta las asistencias del mes actual de un cliente
    public int contarAsistenciasMesActual(String documentoCliente) throws SQLException {
        Validador.validarDocumento(documentoCliente);
        LocalDate hoy = LocalDate.now();
        return asistenciaRepository.countAsistenciasByClienteAndMonth(
                documentoCliente,
                hoy.getMonthValue(),
                hoy.getYear()
        );
    }

    // Verifica si un cliente asistió hoy
    public boolean asistioHoy(String documentoCliente) throws SQLException {
        Validador.validarDocumento(documentoCliente);
        List<Asistencias> asistenciasHoy = asistenciaRepository.findByFecha(LocalDate.now());

        return asistenciasHoy.stream()
                .anyMatch(a -> a.getCliente().getDocumento().equals(documentoCliente));
    }

    // Obtiene el total de asistencias en un día específico
    public int obtenerAsistenciasPorDia(LocalDate fecha) throws SQLException {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        return asistenciaRepository.findByFecha(fecha).size();
    }

    // Obtiene el total de asistencias en el mes actual
    public int obtenerAsistenciasMesActual() throws SQLException {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());

        return obtenerAsistenciasPorRango(inicioMes, finMes);
    }

    // Obtiene el total de asistencias en un rango de fechas
    public int obtenerAsistenciasPorRango(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas son obligatorias");
        }

        int total = 0;
        LocalDate fecha = fechaInicio;

        while (!fecha.isAfter(fechaFin)) {
            total += asistenciaRepository.findByFecha(fecha).size();
            fecha = fecha.plusDays(1);
        }

        return total;
    }

    // Obtiene las asistencias de hoy (para mostrar en tabla)
    public List<Asistencias> obtenerAsistenciasDeHoy() throws SQLException {
        return asistenciaRepository.findByFecha(LocalDate.now());
    }

    // Obtiene las asistencias de una fecha específica (para reportes)
    public List<Asistencias> obtenerAsistenciasPorFecha(LocalDate fecha) throws SQLException {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        return asistenciaRepository.findByFecha(fecha);
    }

    // Obtiene las asistencias de un rango de fechas (para reportes)
    public List<Asistencias> obtenerAsistenciasPorRangoL(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas son obligatorias");
        }

        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha final no puede ser anterior a la inicial");
        }

        List<Asistencias> resultado = new ArrayList<>();
        LocalDate fecha = fechaInicio;

        while (!fecha.isAfter(fechaFin)) {
            resultado.addAll(asistenciaRepository.findByFecha(fecha));
            fecha = fecha.plusDays(1);
        }

        return resultado;
    }

    // Obtiene los clientes con más asistencias (top N)
    public Map<String, Integer> obtenerClientesMasFrecuentes(int limite) throws SQLException {
        List<Asistencias> todasAsistencias = asistenciaRepository.findAll();

        // Contar asistencias por cliente
        Map<String, Integer> conteoAsistencias = new HashMap<>();

        for (Asistencias asistencia : todasAsistencias) {
            String documento = asistencia.getCliente().getDocumento();
            conteoAsistencias.put(documento, conteoAsistencias.getOrDefault(documento, 0) + 1);
        }

        // Ordenar y limitar
        return conteoAsistencias.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limite)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    // Obtiene el total de asistencias hoy
    public int obtenerAsistenciasHoy() throws SQLException {
        return asistenciaRepository.findByFecha(LocalDate.now()).size();
    }

    // Obtiene el promedio de asistencias por día en un mes y año específicos
    public double obtenerPromedioAsistenciasPorDia(int mes, int anio) throws SQLException {
        YearMonth yearMonth = YearMonth.of(anio, mes);
        LocalDate inicioMes = yearMonth.atDay(1);
        LocalDate finMes = yearMonth.atEndOfMonth();

        int totalAsistencias = obtenerAsistenciasPorRango(inicioMes, finMes);
        int diasDelMes = yearMonth.lengthOfMonth();

        return (double) totalAsistencias / diasDelMes;
    }

    // Obtiene las asistencias por día de la semana en el mes actual
    public Map<String, Integer> obtenerAsistenciasPorDiaSemana() throws SQLException {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());

        Map<String, Integer> asistenciasPorDia = new LinkedHashMap<>();
        asistenciasPorDia.put("LUNES", 0);
        asistenciasPorDia.put("MARTES", 0);
        asistenciasPorDia.put("MIERCOLES", 0);
        asistenciasPorDia.put("JUEVES", 0);
        asistenciasPorDia.put("VIERNES", 0);
        asistenciasPorDia.put("SABADO", 0);
        asistenciasPorDia.put("DOMINGO", 0);

        LocalDate fecha = inicioMes;
        while (!fecha.isAfter(finMes)) {
            List<Asistencias> asistenciasDia = asistenciaRepository.findByFecha(fecha);

            String diaSemana = switch (fecha.getDayOfWeek()) {
                case MONDAY -> "LUNES";
                case TUESDAY -> "MARTES";
                case WEDNESDAY -> "MIERCOLES";
                case THURSDAY -> "JUEVES";
                case FRIDAY -> "VIERNES";
                case SATURDAY -> "SABADO";
                case SUNDAY -> "DOMINGO";
            };

            int valorActual = asistenciasPorDia.getOrDefault(diaSemana, 0);
            asistenciasPorDia.put(diaSemana, valorActual + asistenciasDia.size());

            fecha = fecha.plusDays(1);
        }

        return asistenciasPorDia;
    }



    // ==================== ESTADÍSTICAS DE ENTRENADORES ====================

    // Obtiene los entrenadores con más clientes asignados (top N)
    public Map<String, Integer> obtenerEntrenadoresConMasClientes(int limite) throws SQLException {
        List<AsignacionEntrenadores> todasAsignaciones = asignacionEntrenadorRepository.findAll();

        // Contar clientes activos por entrenador
        Map<String, Integer> conteoClientes = new HashMap<>();

        for (AsignacionEntrenadores asignacion : todasAsignaciones) {
            if (asignacion.estaActiva()) {
                String documento = asignacion.getEntrenador().getDocuEntrenador();
                String nombre = asignacion.getEntrenador().getNombreCompleto();
                conteoClientes.put(nombre, conteoClientes.getOrDefault(nombre, 0) + 1);
            }
        }

        // Ordenar y limitar
        return conteoClientes.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limite)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    // Obtiene la distribución de clientes por entrenador
    public Map<String, Integer> obtenerDistribucionClientesPorEntrenador() throws SQLException {
        List<AsignacionEntrenadores> todasAsignaciones = asignacionEntrenadorRepository.findAll();

        Map<String, Integer> distribucion = new HashMap<>();

        for (AsignacionEntrenadores asignacion : todasAsignaciones) {
            if (asignacion.estaActiva()) {
                String nombre = asignacion.getEntrenador().getNombreCompleto();
                distribucion.put(nombre, distribucion.getOrDefault(nombre, 0) + 1);
            }
        }

        return distribucion;
    }

    // Obtiene el total de clientes con entrenador asignado
    public int obtenerTotalClientesConEntrenador() throws SQLException {
        List<AsignacionEntrenadores> asignaciones = asignacionEntrenadorRepository.findAll();

        return (int) asignaciones.stream()
                .filter(AsignacionEntrenadores::estaActiva)
                .count();
    }

    // ==================== ESTADÍSTICAS DE MEMBRESÍAS ====================

    // Obtiene el número de clientes activos por tipo de membresía
    public Map<String, Integer> obtenerClientesActivosPorTipoMembresia() throws SQLException {
        List<MembresiaClientes> todasMembresias = membresiaClienteRepository.findAll();

        // Contar solo membresías ACTIVAS por tipo
        Map<String, Integer> conteoActivas = new HashMap<>();

        for (MembresiaClientes mc : todasMembresias) {
            if (mc.estaActiva()) { // ✅ Solo membresías activas
                String tipo = mc.getMembresia().getTipoMembresia();
                conteoActivas.put(tipo, conteoActivas.getOrDefault(tipo, 0) + 1);
            }
        }

        return conteoActivas;
    }

    /**
     * Versión con límite (top N) de clientes activos por tipo
     */
    public Map<String, Integer> obtenerClientesActivosPorTipoMembresia(int limite) throws SQLException {
        Map<String, Integer> conteoActivas = obtenerClientesActivosPorTipoMembresia();

        // Ordenar y limitar
        return conteoActivas.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limite)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    // Obtiene los ingresos totales por membresías activas
    public Map<String, Double> obtenerIngresosMembresiasActivas() throws SQLException {
        List<MembresiaClientes> membresiaActivas = membresiaClienteRepository.findAll()
                .stream()
                .filter(MembresiaClientes::estaActiva)
                .collect(Collectors.toList());

        Map<String, Double> ingresos = new HashMap<>();

        for (MembresiaClientes mc : membresiaActivas) {
            String tipo = mc.getMembresia().getTipoMembresia();
            Double precio = mc.getMembresia().getPrecioMembresia();
            ingresos.put(tipo, ingresos.getOrDefault(tipo, 0.0) + precio);
        }

        return ingresos;
    }

    // Obtiene las membresías más seleccionadas (top N)
    public Map<String, Integer> obtenerMembresiaMasSeleccionadas(int limite) throws SQLException {
        List<MembresiaClientes> todasMembresias = membresiaClienteRepository.findAll();

        // Contar por tipo de membresía
        Map<String, Integer> conteoMembresias = new HashMap<>();

        for (MembresiaClientes mc : todasMembresias) {
            String tipo = mc.getMembresia().getTipoMembresia();
            conteoMembresias.put(tipo, conteoMembresias.getOrDefault(tipo, 0) + 1);
        }

        // Ordenar y limitar
        return conteoMembresias.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limite)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    // Obtiene el total de membresías activas
    public int obtenerTotalMembresiasActivas() throws SQLException {
        List<MembresiaClientes> todasMembresias = membresiaClienteRepository.findAll();

        return (int) todasMembresias.stream()
                .filter(MembresiaClientes::estaActiva)
                .count();
    }

    // Obtiene el total de membresías vencidas
    public int obtenerTotalMembresiasVencidas() throws SQLException {
        return membresiaClienteRepository.findMembresiasVencidas().size();
    }

    // Obtiene el total de membresías próximas a vencer en los próximos 7 días
    public int obtenerMembresiasProximasAVencer() throws SQLException {
        return membresiaClienteRepository.findMembresiasProximasAVencer(7).size();
    }

    // Calcula la tasa de renovación de membresías
    public double calcularTasaRenovacion() throws SQLException {
        List<MembresiaClientes> todasMembresias = membresiaClienteRepository.findAll();

        // Contar clientes con múltiples membresías (renovaron)
        Map<String, Integer> membresiasPorCliente = new HashMap<>();

        for (MembresiaClientes mc : todasMembresias) {
            String documento = mc.getCliente().getDocumento();
            membresiasPorCliente.put(documento, membresiasPorCliente.getOrDefault(documento, 0) + 1);
        }

        long clientesQueRenovaron = membresiasPorCliente.values().stream()
                .filter(count -> count > 1)
                .count();

        int totalClientes = membresiasPorCliente.size();

        if (totalClientes == 0) return 0.0;

        return (double) clientesQueRenovaron / totalClientes * 100;
    }

    // Obtiene los ingresos estimados por tipo de membresía
    public Map<String, Double> obtenerIngresosPorTipoMembresia() throws SQLException {
        List<MembresiaClientes> membresiaActivas = membresiaClienteRepository.findAll()
                .stream()
                .filter(MembresiaClientes::estaActiva)
                .collect(Collectors.toList());

        Map<String, Double> ingresos = new HashMap<>();

        for (MembresiaClientes mc : membresiaActivas) {
            String tipo = mc.getMembresia().getTipoMembresia();
            Double precio = mc.getMembresia().getPrecioMembresia();
            ingresos.put(tipo, ingresos.getOrDefault(tipo, 0.0) + precio);
        }

        return ingresos;
    }

    // Obtiene el ingreso total estimado por membresías
    public double obtenerIngresoTotalMembresias() throws SQLException {
        return obtenerIngresosPorTipoMembresia().values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }
//
//    //Obtiene clientes con membresías vencidas
//    public List<MembresiaClientes> obtenerMembresiasVencidas() throws SQLException {
//        return membresiaClienteRepository.findMembresiasVencidas();
//    }
//
    //Calcula días restantes de membresía
    public long calcularDiasRestantes(String documentoCliente) throws SQLException {
        Validador.validarDocumento(documentoCliente);

        Optional<MembresiaClientes> membresiaOpt =
                membresiaClienteRepository.findMembresiaActivaByCliente(documentoCliente);

        if (!membresiaOpt.isPresent()) {
            return 0;
        }

        return CalculadoraFechas.calcularDiasRestantes(membresiaOpt.get().getFechaFinalizacion());
    }

    public List<MembresiaData> obtenerDistribucionMembresiasConPorcentajes() throws SQLException {
        Map<String, Integer> distribucion = obtenerClientesActivosPorTipoMembresia();
        Map<String, Double> ingresos = obtenerIngresosPorTipoMembresia();

        // Calcular total de ingresos
        double totalIngresos = ingresos.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        List<MembresiaData> resultado = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : distribucion.entrySet()) {
            String tipo = entry.getKey();
            int cantidad = entry.getValue();
            double ingreso = ingresos.getOrDefault(tipo, 0.0);

            // Calcular porcentaje sobre el total de INGRESOS, no sobre cantidad
            double porcentaje = (totalIngresos > 0) ? (ingreso / totalIngresos) * 100 : 0;

            resultado.add(new MembresiaData(tipo, cantidad, ingreso, porcentaje));
        }

        // Ordenar por ingresos (descendente)
        resultado.sort((m1, m2) -> Double.compare(m2.getIngresos(), m1.getIngresos()));

        return resultado;
    }

    // ==================== ESTADÍSTICAS DE CLIENTES ====================

    // Obtiene el total de clientes registrados
    public int obtenerTotalClientes() throws SQLException {
        return clienteRepository.findAll().size();
    }

    // Obtiene el total de clientes activos (con membresía activa)
    public int obtenerClientesActivos() throws SQLException {
        return obtenerTotalMembresiasActivas();
    }

    // Obtiene el total de clientes inactivos (sin membresía activa)
    public int obtenerClientesInactivos() throws SQLException {
        int totalClientes = obtenerTotalClientes();
        int clientesActivos = obtenerClientesActivos();
        return totalClientes - clientesActivos;
    }

    // Obtiene el número de nuevos clientes registrados en el mes actual
    public int obtenerNuevosClientesMes() throws SQLException {
        List<Clientes> todosClientes = clienteRepository.findAll();
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);

        return (int) todosClientes.stream()
                .filter(c -> c.getFechaRegistro() != null &&
                        !c.getFechaRegistro().isBefore(inicioMes))
                .count();
    }

    // Obtiene la distribución de clientes por barrio
    public Map<String, Integer> obtenerClientesPorBarrio() throws SQLException {
        List<Clientes> todosClientes = clienteRepository.findAll();

        Map<String, Integer> distribucion = new HashMap<>();

        for (Clientes cliente : todosClientes) {
            if (cliente.getBarrio() != null) {
                String barrio = cliente.getBarrio().getNombreBarrio();
                distribucion.put(barrio, distribucion.getOrDefault(barrio, 0) + 1);
            }
        }

        return distribucion.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public List<TopClienteData> obtenerTopClientesConNombres(int limite) throws SQLException {
        List<Asistencias> todasAsistencias = asistenciaRepository.findAll();

        // Contar asistencias por cliente
        Map<String, Integer> conteoAsistencias = new HashMap<>();

        for (Asistencias asistencia : todasAsistencias) {
            String documento = asistencia.getCliente().getDocumento();
            conteoAsistencias.put(documento, conteoAsistencias.getOrDefault(documento, 0) + 1);
        }

        // Ordenar por cantidad de asistencias (descendente)
        List<Map.Entry<String, Integer>> listaOrdenada = conteoAsistencias.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limite)
                .collect(Collectors.toList());

        // Crear lista de TopClienteData con nombres
        List<TopClienteData> resultado = new ArrayList<>();
        int posicion = 1;

        for (Map.Entry<String, Integer> entry : listaOrdenada) {
            String documento = entry.getKey();
            int cantidadAsistencias = entry.getValue();

            // Buscar el cliente para obtener su nombre
            Optional<Clientes> clienteOpt = clienteRepository.findByDocumento(documento);

            String nombreCompleto = clienteOpt
                    .map(c -> c.getNombres() + " " + c.getApellidos())
                    .orElse("Cliente Desconocido");

            // Obtener emoji de posición
            String emoji = obtenerEmojiPosicion(posicion);

            resultado.add(new TopClienteData(
                    emoji,
                    documento,
                    nombreCompleto,
                    cantidadAsistencias
            ));

            posicion++;
        }

        return resultado;
    }

    private String obtenerEmojiPosicion(int posicion) {
        switch (posicion) {
            case 1: return "🥇";
            case 2: return "🥈";
            case 3: return "🥉";
            default: return String.format("%2d.", posicion);
        }
    }

    // ==================== ESTADÍSTICAS DE RUTINAS ====================

    // Obtiene el total de clientes asignados a una rutina específica
    public int obtenerClientesAsignadosARutina(Integer idRutina) throws SQLException {
        if (idRutina == null) {
            throw new IllegalArgumentException("El ID de la rutina es obligatorio");
        }

        List<RutinaAsignadas> asignaciones = rutinaAsignadaRepository.findByRutina(idRutina);

        // Contar solo las asignaciones activas
        return (int) asignaciones.stream()
                .filter(RutinaAsignadas::estaActiva)
                .count();
    }

    //Obtiene el total de clientes con rutina asignada
    public int obtenerTotalClientesConRutina() throws SQLException {
        List<RutinaAsignadas> todasAsignaciones = rutinaAsignadaRepository.findAll();

        // Obtener documentos únicos de clientes con rutinas activas
        Set<String> clientesUnicos = todasAsignaciones.stream()
                .filter(RutinaAsignadas::estaActiva)
                .map(ra -> ra.getCliente().getDocumento())
                .collect(Collectors.toSet());

        return clientesUnicos.size();
    }

    // Obtiene las rutinas más asignadas (top N)
    public Map<String, Integer> obtenerRutinasMasAsignadas(int limite) throws SQLException {
        List<RutinaAsignadas> todasAsignaciones = rutinaAsignadaRepository.findAll();

        // Contar asignaciones activas por rutina
        Map<String, Integer> conteoRutinas = new HashMap<>();

        for (RutinaAsignadas asignacion : todasAsignaciones) {
            if (asignacion.estaActiva()) {
                String objetivo = asignacion.getRutina().getObjetivo();
                conteoRutinas.put(objetivo, conteoRutinas.getOrDefault(objetivo, 0) + 1);
            }
        }

        // Ordenar y limitar
        return conteoRutinas.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limite)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    // ==================== RESUMEN GENERAL ====================

    // Obtiene un resumen general de estadísticas clave
    public Map<String, Object> obtenerResumenGeneral() throws SQLException {
        Map<String, Object> resumen = new LinkedHashMap<>();

        // Clientes
        resumen.put("totalClientes", obtenerTotalClientes());
        resumen.put("clientesActivos", obtenerClientesActivos());
        resumen.put("clientesInactivos", obtenerClientesInactivos());
        resumen.put("nuevosClientesMes", obtenerNuevosClientesMes());

        // Membresías
        resumen.put("membresiasActivas", obtenerTotalMembresiasActivas());
        resumen.put("membresiasVencidas", obtenerTotalMembresiasVencidas());
        resumen.put("membresiasProximasVencer", obtenerMembresiasProximasAVencer());
        resumen.put("tasaRenovacion", calcularTasaRenovacion());

        // Asistencias
        resumen.put("asistenciasHoy", obtenerAsistenciasHoy());
        resumen.put("asistenciasMes", obtenerAsistenciasMesActual());

        // Entrenadores
        resumen.put("clientesConEntrenador", obtenerTotalClientesConEntrenador());

        // Ingresos
        resumen.put("ingresosTotales", obtenerIngresoTotalMembresias());

        return resumen;
    }
}
