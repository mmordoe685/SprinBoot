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

    //Constructor con inyección de dependencias de la clase UsuarioRepository
    public AdminController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

   
    // PANTALLA PRINCIPAL DEL PANEL DE ADMIN
   
    @GetMapping
    public String dashboard() {
        // Solo devuelve la vista admin-dashboard.html
        return "admin-dashboard";
    }

   
    // LISTADO DE USUARIOS (ADMIN)
    // URL: /admin/usuarios

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {

        // Pedimos todos los usuarios a la BD
        List<Usuario> usuarios = usuarioRepository.findAll();

        // Los guardamos en el modelo para la vista
        model.addAttribute("usuarios", usuarios);

        // Devolvemos la vista admin-usuarios.html
        return "admin-usuarios";
    }

  
    // VER DETALLE DE UN USUARIO
    // URL: /admin/usuarios/{id}/ver

    @GetMapping("/usuarios/{id}/ver")
    public String verUsuario(@PathVariable Integer id, Model model) {
        // Miramos el repositorio de usuarios para encontrar un uusario
        // con el id indicado
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

    
        if (usuarioOpt.isEmpty()) {
            // Si el usuario no existe, volvemos al listado
            return "redirect:/admin/usuarios";
        }

        // Si existe, lo metemos en el modelo
        model.addAttribute("usuario", usuarioOpt.get());

       
        return "usuarioVista";
    }

    
    // FORMULARIO NUEVO USUARIO
    // URL: /admin/usuarios/nuevo
    // Este metodo muestra un formulario para crear un nuevo usuario
    public String mostrarFormularioNuevo(Model model) {

        // Usuario vacío para el formulario
        model.addAttribute("usuario", new Usuario());

        // Vista del formulario para crear (nuevoUsuario.html)
        return "nuevoUsuario";
    }

   
    // GUARDAR NUEVO USUARIO
    // URL: /admin/usuarios/guardar  (POST)
    // Guardamos el nuevo usuario en la base de datos
    @PostMapping("/usuarios/guardar")
    public String guardarNuevo(@ModelAttribute Usuario usuario) {

        // Guardamos el usuario tal cual llega del formulario
        usuarioRepository.save(usuario);

        // Volvemos al listado de usuarios y lo vera con el nuevo usuario
        return "redirect:/admin/usuarios";
    }

  
    // FORMULARIO EDITAR USUARIO
    // URL: /admin/usuarios/{id}/editar  (GET)
  
    @GetMapping("/usuarios/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        // Buscamos el usuario con el ID proporcionado en la url
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isEmpty()) {
            // Si no existe, volvemos al listado
            return "redirect:/admin/usuarios";
        }

        // Usuario encontrado: lo metemos en el modelo y mostramos el formulario
        model.addAttribute("usuario", usuarioOpt.get());

       // Agregamos el atributo es nuevo al modelo 
        model.addAttribute("esNuevo", false);

        // Volvemos a la vista del formulario de edición (editar.html)
        return "editar";
    }

   
    // URL: /admin/usuarios/{id}/editar  (POST)
    
    @PostMapping("/usuarios/{id}/editar")
    public String actualizarUsuario(@PathVariable Integer id,
                                    @ModelAttribute Usuario formUsuario) {
        // Buscamos el usuario con el ID proporcionado en la URL.
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isEmpty()) {
            // Si no existe, volvemos al listado
            return "redirect:/admin/usuarios";
        }

        // Si el usuario existe obtememos todos su datos
        Usuario usuarioBD = usuarioOpt.get();

        // Actualizamos solo los campos modificados en el formulario
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

    
    // ELIMINAR USUARIO
    // URL: /admin/usuarios/{id}/eliminar
    // Metodo para la eliminación de un usuario por su ID
    @GetMapping("/usuarios/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Integer id) {
        // Comprobamos si el usuario existe
        if (usuarioRepository.existsById(id)) {
            // Si existe, lo eliminamos
            usuarioRepository.deleteById(id);
        }

        // volvemos al listado
        return "redirect:/admin/usuarios";
    }

    
    // EXPORTAR USUARIOS A EXCEL
    // URL: /admin/usuarios/export/excel
    
    @GetMapping("/usuarios/export/excel")
    public ResponseEntity<byte[]> exportUsuariosExcel() throws Exception {

        //  Obtenemos todos los usuarios
        List<Usuario> usuarios = usuarioRepository.findAll();

        //  Creamos el libro Excel
        Workbook workbook = new XSSFWorkbook();

        // La hoja la llamaremos Usuarios 
        Sheet sheet = workbook.createSheet("Usuarios");

        // Las columnas
        String[] columnas = {
                "ID",
                "Nombre",
                "Email",
                "Rol",
                "País",
                "FotoURL",
                "FechaRegistro"
        };

        //  Crea la primera fila (cabecera) en la hoja de Excel
        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < columnas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnas[i]);
        }

        // Rellenar una fila por cada usuario
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
            // Si la fecha de registro no es nula, la convertimos a texto
            if (usuario.getFechaRegistro() != null) {

                fechaRegistroTexto = usuario.getFechaRegistro().toString();
            }
            // Colocamos la fecha de registro en la celda de la columna 6
            dataRow.createCell(6).setCellValue(fechaRegistroTexto);
           // Pasamos a la siguiente fila
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

       @GetMapping("/")
    public String inicio() {
        return "index"; // Redirige a src/main/resources/templates/index.html
    }
}

