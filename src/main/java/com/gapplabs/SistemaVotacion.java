package com.gapplabs;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SistemaVotacion extends JFrame {
    public SistemaVotacion() {
        setTitle("Sistema de Votación");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();

        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem menuItemSalir = new JMenuItem("Salir");
        menuItemSalir.addActionListener(e -> System.exit(0));
        menuArchivo.add(menuItemSalir);

        JMenu menuAdministracion = new JMenu("Administración");
        JMenuItem menuItemMesas = new JMenuItem("Mesas");
        menuItemMesas.addActionListener(e -> mostrarDialogoMesas());
        menuAdministracion.add(menuItemMesas);
        JMenuItem menuItemCursos = new JMenuItem("Cursos");
        menuItemCursos.addActionListener(e -> mostrarVentanaCursos());
        menuAdministracion.add(menuItemCursos);
        JMenuItem menuItemEstudiantes = new JMenuItem("Estudiantes");
        menuItemEstudiantes.addActionListener(e -> mostrarDialogoAgregarEstudiante());
        menuAdministracion.add(menuItemEstudiantes);
        JMenuItem menuItemCandidatos = new JMenuItem("Candidatos");
        menuItemCandidatos.addActionListener(e -> mostrarDialogoAgregarCandidato());
        menuAdministracion.add(menuItemCandidatos);

        JMenu menuProceso = new JMenu("Proceso");
        JMenuItem menuItemSufragar = new JMenuItem("Sufragar");
        menuItemSufragar.addActionListener(e -> mostrarPadronElectoral());
        menuProceso.add(menuItemSufragar);

        JMenu menuReportes = new JMenu("Reportes");
        JMenuItem menuItemListaCandidatos = new JMenuItem("Padrón electoral");
        menuItemListaCandidatos.addActionListener(e -> mostrarListaCandidatos());
        menuReportes.add(menuItemListaCandidatos);

        JMenuItem menuItemResultadosGenerales = new JMenuItem("Resultados generales");
        menuItemResultadosGenerales.addActionListener(e -> mostrarResultadosGenerales());
        menuReportes.add(menuItemResultadosGenerales);

        JMenuItem menuItemResultadosPorMesas = new JMenuItem("Resultados por mesas");
        menuItemResultadosPorMesas.addActionListener(e -> mostrarResultadosPorMesas());
        menuReportes.add(menuItemResultadosPorMesas);

        JMenuItem menuItemResultadosGeneralesGrafico = new JMenuItem("Resultados generales en gráfico de barras");
        menuItemResultadosGeneralesGrafico.addActionListener(e -> mostrarResultadosGeneralesGrafico());
        menuReportes.add(menuItemResultadosGeneralesGrafico);

        menuBar.add(menuArchivo);
        menuBar.add(menuAdministracion);
        menuBar.add(menuProceso);
        menuBar.add(menuReportes);

        setJMenuBar(menuBar);
    }

    private void mostrarResultadosPorMesas() {
        Map<String, Map<Candidato, Integer>> resultadosPorMesa = new HashMap<>();
        for (Candidato candidato : candidatos) {
            for (Voto voto : candidato.votos) {
                String mesa = voto.getMesa();
                resultadosPorMesa.putIfAbsent(mesa, new HashMap<>());
                Map<Candidato, Integer> conteoPorCandidato = resultadosPorMesa.get(mesa);
                conteoPorCandidato.merge(candidato, 1, Integer::sum);
            }
        }
        StringBuilder resultados = new StringBuilder("Resultados por Mesa:\n\n");
        for (Map.Entry<String, Map<Candidato, Integer>> entradaMesa : resultadosPorMesa.entrySet()) {
            String mesa = entradaMesa.getKey();
            Map<Candidato, Integer> resultadosCandidatos = entradaMesa.getValue();
            resultados.append("Mesa: ").append(mesa).append("\n");
            for (Map.Entry<Candidato, Integer> entradaCandidato : resultadosCandidatos.entrySet()) {
                Candidato candidato = entradaCandidato.getKey();
                Integer votos = entradaCandidato.getValue();
                resultados.append(String.format("    %s %s (%s) - Votos: %d\n", candidato.nombre, candidato.apellido, candidato.partido, votos));
            }
            resultados.append("\n");
        }
        JOptionPane.showMessageDialog(null, resultados.toString(), "Resultados por Mesa", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarListaCandidatos() {
        String[] columnNames = {"Nombre", "Apellido", "Partido"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        for (Candidato candidato : candidatos) {
            Object[] row = {candidato.nombre, candidato.apellido, candidato.partido};
            tableModel.addRow(row);
        }
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        JFrame frame = new JFrame("Padrón electoral");
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void mostrarResultadosGeneralesGrafico() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Candidato candidato : candidatos) {
            String nombreCandidato = candidato.nombre + " " + candidato.apellido;
            dataset.addValue(candidato.votos.size(), "Votos", nombreCandidato);
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Resultados Generales",
                "Candidatos",
                "Votos",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        JFrame frame = new JFrame("Resultados Generales en Gráfico de Barras");
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void mostrarResultadosGenerales() {
        StringBuilder resultados = new StringBuilder("Resultados Generales:\n\n");
        for (Candidato candidato : candidatos) {
            resultados.append(candidato.nombre).append(" ").append(candidato.apellido)
                    .append(" - Partido: ").append(candidato.partido)
                    .append(" - Votos: ").append(candidato.votos.size()).append("\n");
            for (Voto voto : candidato.votos) {
                Estudiante estudiante = voto.getEstudiante();
                String infoVotante = String.format("    Votante: %s %s, Cédula: %s, Curso: %s\n",
                        estudiante.nombre, estudiante.apellido,
                        estudiante.cedula, estudiante.curso);
                resultados.append(infoVotante);
            }
            resultados.append("\n");
        }
        JOptionPane.showMessageDialog(null, resultados.toString(), "Resultados Generales", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarPadronElectoral() {
        JFrame ventanaPadron = new JFrame("Sufragar");
        ventanaPadron.setSize(400, 600);
        ventanaPadron.setLocationRelativeTo(null);
        DefaultListModel<String> modeloLista = new DefaultListModel<>();
        for (Candidato candidato : candidatos) {
            modeloLista.addElement(candidato.nombre + " " + candidato.apellido + " - " + candidato.partido);
        }
        JList<String> listaCandidatos = new JList<>(modeloLista);
        JScrollPane scrollPane = new JScrollPane(listaCandidatos);
        ventanaPadron.add(scrollPane, BorderLayout.CENTER);
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton botonVotar = new JButton("Votar");
        configuraBotonVotar(botonVotar, listaCandidatos, ventanaPadron);
        panelBotones.add(botonVotar);
        ventanaPadron.add(panelBotones, BorderLayout.SOUTH);
        ventanaPadron.setVisible(true);
    }

    private void configuraBotonVotar(JButton botonVotar, JList<String> listaCandidatos, JFrame ventanaPadron) {
        botonVotar.setBackground(new Color(100, 149, 237));
        botonVotar.setForeground(Color.WHITE);
        botonVotar.setFont(new Font("Arial", Font.BOLD, 16));
        botonVotar.setFocusPainted(false);
        botonVotar.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        botonVotar.addActionListener(e -> {
            String cedula = JOptionPane.showInputDialog(ventanaPadron, "Ingrese su cédula:");
            if (cedula == null || cedula.trim().isEmpty()) {
                JOptionPane.showMessageDialog(ventanaPadron, "Debe ingresar una cédula válida.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String[] opcionesMesas = {"Mesa 1", "Mesa 2", "Mesa 3", "Mesa 4"};
            String mesaSeleccionada = (String) JOptionPane.showInputDialog(ventanaPadron, "Seleccione la mesa:", "Mesas", JOptionPane.QUESTION_MESSAGE, null, opcionesMesas, opcionesMesas[0]);
            if (mesaSeleccionada == null) {
                return;
            }
            int indexCandidato = listaCandidatos.getSelectedIndex();
            if (indexCandidato == -1) {
                JOptionPane.showMessageDialog(ventanaPadron, "Debe seleccionar un candidato.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Estudiante estudianteSeleccionado = obtenerEstudiantePorCedula(cedula);
            if (estudianteSeleccionado == null) {
                JOptionPane.showMessageDialog(ventanaPadron, "Estudiante no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Candidato candidatoSeleccionado = candidatos.get(indexCandidato);
            Voto voto = new Voto(estudianteSeleccionado, mesaSeleccionada);
            candidatoSeleccionado.agregarVoto(voto);
            JOptionPane.showMessageDialog(ventanaPadron, "Voto registrado para " + candidatoSeleccionado.nombre + " " + candidatoSeleccionado.apellido + " en " + mesaSeleccionada);
        });
    }

    class Candidato {
        String nombre;
        String apellido;
        String partido;
        List<Voto> votos;

        public Candidato(String nombre, String apellido, String partido) {
            this.nombre = nombre;
            this.apellido = apellido;
            this.partido = partido;
            this.votos = new ArrayList<>();
        }

        public void agregarVoto(Voto voto) {
            votos.add(voto);
        }
    }

    class Voto {
        Estudiante estudiante;
        String mesa;

        public Voto(Estudiante estudiante, String mesa) {
            this.estudiante = estudiante;
            this.mesa = mesa;
        }

        public Estudiante getEstudiante() {
            return estudiante;
        }

        public String getMesa() {
            return mesa;
        }
    }

    class Estudiante {
        String nombre;
        String apellido;
        String curso;
        String cedula;

        public Estudiante(String nombre, String apellido, String curso, String cedula) {
            this.nombre = nombre;
            this.apellido = apellido;
            this.curso = curso;
            this.cedula = cedula;
        }

        public String getCedula() {
            return cedula;
        }

        @Override
        public String toString() {
            return nombre + " " + apellido + " - " + curso;
        }
    }

    List<Estudiante> estudiantes = new ArrayList<>();
    List<Candidato> candidatos = new ArrayList<>();

    private void mostrarVentanaCursos() {
        JFrame ventanaCursos = new JFrame("Lista de Cursos");
        ventanaCursos.setSize(300, 300);
        ventanaCursos.setLocationRelativeTo(null);
        String[] cursos = {"- Primer Grado de EGB", "- Segundo Grado de EGB", "- Tercer Grado de EGB",
                "- Cuarto Grado de EGB", "- Quinto Grado de EGB", "- Sexto Grado de EGB",
                "- Séptimo Grado de EGB", "- Octavo Grado de EGB", "- Noveno Grado de EGB",
                "- Décimo Grado de EGB", "- Primero de Bachillerato", "- Segundo de Bachillerato", "- Tercero de Bachillerato "};
        JList<String> listaCursos = new JList<>(cursos);
        listaCursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaCursos.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(listaCursos);
        ventanaCursos.add(scrollPane);
        ventanaCursos.setVisible(true);
    }

    private void mostrarDialogoAgregarCandidato() {
        JTextField campoNombre = new JTextField();
        JTextField campoApellido = new JTextField();
        JTextField campoPartido = new JTextField();
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Nombre:"));
        panel.add(campoNombre);
        panel.add(new JLabel("Apellido:"));
        panel.add(campoApellido);
        panel.add(new JLabel("Partido:"));
        panel.add(campoPartido);
        int resultado = JOptionPane.showConfirmDialog(this, panel, "Agregar Candidato", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resultado == JOptionPane.OK_OPTION) {
            String nombre = campoNombre.getText();
            String apellido = campoApellido.getText();
            String partido = campoPartido.getText();
            candidatos.add(new Candidato(nombre, apellido, partido));
            JOptionPane.showMessageDialog(this, "Candidato agregado: " + nombre + " " + apellido + ", Partido: " + partido);
            System.out.println("Candidato añadido: " + nombre + " " + apellido + ", Partido: " + partido);
        }
    }

    private Estudiante obtenerEstudiantePorCedula(String cedula) {
        for (Estudiante estudiante : estudiantes) {
            if (estudiante.getCedula().equals(cedula)) {
                return estudiante;
            }
        }
        return null;
    }

    private void mostrarDialogoAgregarEstudiante() {
        JTextField campoNombre = new JTextField();
        JTextField campoApellido = new JTextField();
        JTextField campoCedula = new JTextField();
        JComboBox<String> comboCursos = new JComboBox<>(new String[]{"Cuarto Grado de EGB", "Quinto Grado de EGB",
                "Sexto Grado de EGB", "Séptimo Grado de EGB", "Octavo Grado de EGB",
                "Noveno Grado de EGB", "Décimo Grado de EGB", "Primero de Bachillerato",
                "Segundo de Bachillerato", "Tercero de Bachillerato "});
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Nombre:"));
        panel.add(campoNombre);
        panel.add(new JLabel("Apellido:"));
        panel.add(campoApellido);
        panel.add(new JLabel("Cédula:"));
        panel.add(campoCedula);
        panel.add(new JLabel("Curso:"));
        panel.add(comboCursos);
        int resultado = JOptionPane.showConfirmDialog(null, panel, "Agregar Estudiante", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resultado == JOptionPane.OK_OPTION) {
            String nombre = campoNombre.getText();
            String apellido = campoApellido.getText();
            String curso = (String) comboCursos.getSelectedItem();
            String cedula = campoCedula.getText();
            if (!cedula.isEmpty()) {
                estudiantes.add(new Estudiante(nombre, apellido, curso, cedula));
                JOptionPane.showMessageDialog(this, "Estudiante agregado: " + nombre + " " + apellido + ", Curso: " + curso + ", Cédula: " + cedula);
            } else {
                JOptionPane.showMessageDialog(this, "Debe ingresar una cédula válida.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarDialogoMesas() {
        JFrame ventanaMesas = new JFrame("Lista de Mesas");
        ventanaMesas.setSize(300, 300);
        ventanaMesas.setLocationRelativeTo(null);
        String[] mesas = {"Mesa 1", "Mesa 2", "Mesa 3", "Mesa 4"};
        JList<String> listaMesas = new JList<>(mesas);
        listaMesas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaMesas.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(listaMesas);
        ventanaMesas.add(scrollPane);
        ventanaMesas.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SistemaVotacion ventanaPrincipal = new SistemaVotacion();
            ventanaPrincipal.setVisible(true);
        });
    }
}
