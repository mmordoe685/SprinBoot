package com.biblioteca.libreria.controller;

import com.biblioteca.libreria.model.Usuario;
import com.biblioteca.libreria.repository.UsuarioRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")   // Todo aquí empieza con /admin/...
public class AdminController {

    // Repositorio para acceder a la tabla de usuarios
    private final UsuarioRepository usuarioRepository;

    // Inyección por constructor (simple y clara)
    public AdminController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // ======================================================
    // PANTALLA PRINCIPAL DEL PANEL DE ADMIN
    // ======================================================
    @GetMapping
    public String dashboard() {
        // Solo devuelve la vista admin-dashboard.html
        return "admin-dashboard";
    }

    // ======================================================
    // LISTADO DE USUARIOS (ADMIN)
    // URL: /admin/usuarios
    // ======================================================
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {

        // Pedimos todos los usuarios a la BD
        List<Usuario> usuarios = usuarioRepository.findAll();

        // Los guardamos en el modelo para la vista
        model.addAttribute("usuarios", usuarios);

        // Devolvemos la vista admin-usuarios.html
        return "admin-usuarios";
    }

    // ======================================================
    // VER DETALLE DE UN USUARIO
    // URL: /admin/usuarios/{id}/ver
    // ======================================================
    @GetMapping("/usuarios/{id}/ver")
    public String verUsuario(@PathVariable Integer id, Model model) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isEmpty()) {
            // Si no existe, volvemos al listado
            return "redirect:/admin/usuarios";
        }

        // Si existe, lo metemos en el modelo
        model.addAttribute("usuario", usuarioOpt.get());

        // Vista con los datos del usuario (por ejemplo usuarioVista.html)
        return "usuarioVista";
    }

    // ======================================================
    // FORMULARIO NUEVO USUARIO
    // URL: /admin/usuarios/nuevo
    // ======================================================
    @GetMapping("/usuarios/nuevo")
    public String mostrarFormularioNuevo(Model model) {

        // Usuario vacío para el formulario
        model.addAttribute("usuario", new Usuario());

        // Vista del formulario para crear (nuevoUsuario.html)
        return "nuevoUsuario";
    }

    // ======================================================
    // GUARDAR NUEVO USUARIO
    // URL: /admin/usuarios/guardar  (POST)
    // ======================================================
    @PostMapping("/usuarios/guardar")
    public String guardarNuevo(@ModelAttribute Usuario usuario) {

        // Guardamos el usuario tal cual llega del formulario
        usuarioRepository.save(usuario);

        // Volvemos al listado de usuarios
        return "redirect:/admin/usuarios";
    }

    // ======================================================
    // FORMULARIO EDITAR USUARIO
    // URL: /admin/usuarios/{id}/editar  (GET)
    // ======================================================
    @GetMapping("/usuarios/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isEmpty()) {
            // Si no existe, volvemos al listado
            return "redirect:/admin/usuarios";
        }

        // Usuario encontrado → lo pasamos al formulario
        model.addAttribute("usuario", usuarioOpt.get());

        // Marcamos que es edición (si lo necesitas en la vista)
        model.addAttribute("esNuevo", false);

        // Vista del formulario de edición (editar.html)
        return "editar";
    }

    // ======================================================
    // ACTUALIZAR USUARIO
    // URL: /admin/usuarios/{id}/editar  (POST)
    // ======================================================
    @PostMapping("/usuarios/{id}/editar")
    public String actualizarUsuario(@PathVariable Integer id,
                                    @ModelAttribute Usuario formUsuario) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isEmpty()) {
            // Si no existe, volvemos al listado
            return "redirect:/admin/usuarios";
        }

        // Usuario existente en BD
        Usuario usuarioBD = usuarioOpt.get();

        // Actualizamos solo los campos básicos
        usuarioBD.setNombre(formUsuario.getNombre());
        usuarioBD.setEmail(formUsuario.getEmail());
        usuarioBD.setContrasena(formUsuario.getContrasena());
        usuarioBD.setTipoUsuario(formUsuario.getTipoUsuario());
        usuarioBD.setFotoUrl(formUsuario.getFotoUrl());
        usuarioBD.setPais(formUsuario.getPais());

        // Guardamos cambios
        usuarioRepository.save(usuarioBD);

        // Volvemos al listado
        return "redirect:/admin/usuarios";
    }

    // ======================================================
    // ELIMINAR USUARIO
    // URL: /admin/usuarios/{id}/eliminar
    // ======================================================
    @GetMapping("/usuarios/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Integer id) {

        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
        }

        // Siempre volvemos al listado
        return "redirect:/admin/usuarios";
    }

    // ======================================================
    // EXPORTAR USUARIOS A EXCEL
    // URL: /admin/usuarios/export/excel
    // ======================================================
    @GetMapping("/usuarios/export/excel")
    public ResponseEntity<byte[]> exportUsuariosExcel() throws Exception {

        // 1. Obtenemos todos los usuarios
        List<Usuario> usuarios = usuarioRepository.findAll();

        // 2. Creamos el libro Excel
        Workbook workbook = new XSSFWorkbook();

        // 3. Hoja llamada "Usuarios"
        Sheet sheet = workbook.createSheet("Usuarios");

        // 4. Cabeceras de las columnas
        String[] columnas = {
                "ID",
                "Nombre",
                "Email",
                "Rol",
                "País",
                "FotoURL",
                "FechaRegistro"
        };

        // 5. Primera fila (fila 0) → cabecera
        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < columnas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnas[i]);
        }

        // 6. Rellenar una fila por cada usuario
        int rowIndex = 1; // empezamos en la fila 1

        for (Usuario usuario : usuarios) {

            Row dataRow = sheet.createRow(rowIndex);

            // ID
            if (usuario.getIdUsuario() != null) {
                dataRow.createCell(0).setCellValue(usuario.getIdUsuario());
            } else {
                dataRow.createCell(0).setCellValue(0);
            }

            // Nombre
            dataRow.createCell(1).setCellValue(
                    value(usuario.getNombre())
            );

            // Email
            dataRow.createCell(2).setCellValue(
                    value(usuario.getEmail())
            );

            // Rol
            dataRow.createCell(3).setCellValue(
                    value(usuario.getTipoUsuario())
            );

            // País
            dataRow.createCell(4).setCellValue(
                    value(usuario.getPais())
            );

            // Foto URL
            dataRow.createCell(5).setCellValue(
                    value(usuario.getFotoUrl())
            );

            // Fecha de registro como texto
            String fechaRegistroTexto = "";
            if (usuario.getFechaRegistro() != null) {
                fechaRegistroTexto = usuario.getFechaRegistro().toString();
            }
            dataRow.createCell(6).setCellValue(fechaRegistroTexto);

            rowIndex++;
        }

        // 7. Ajustar ancho de columnas
        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // 8. Escribir el libro a un array de bytes
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        byte[] bytesExcel = out.toByteArray();

        // 9. Devolver el Excel como descarga
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=usuarios.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytesExcel);
    }

    // Método auxiliar para evitar null en las celdas
    private String value(String s) {
        return (s == null) ? "" : s;
    }
}
