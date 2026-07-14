using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using ExamenApi.Infrastructure.Data;
using ExamenApi.Domain.Entities;
using ExamenApi.Application.DTOs;

namespace ExamenApi.API.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class UsuariosController : ControllerBase
    {
        private readonly AppDbContext _context;

        public UsuariosController(AppDbContext context)
        {
            _context = context;
        }

        [HttpPost("registrar")]
        public async Task<IActionResult> Registrar([FromBody] RegistrarUsuarioDto dto)
        {
            if (await _context.Usuarios.AnyAsync(u => u.Email == dto.Email))
                return BadRequest("El correo ya está registrado.");

            var nuevoUsuario = new Usuario
            {
                Email = dto.Email,
                PasswordHash = dto.Password 
            };

            _context.Usuarios.Add(nuevoUsuario);
            await _context.SaveChangesAsync();

            return Ok(new AuthResponseDto { 
                Id = nuevoUsuario.Id, 
                Email = nuevoUsuario.Email, 
                Mensaje = "Usuario creado exitosamente" 
            });
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] LoginUsuarioDto dto)
        {
            var usuario = await _context.Usuarios
                .FirstOrDefaultAsync(u => u.Email == dto.Email && u.PasswordHash == dto.Password);

            if (usuario == null)
                return Unauthorized("Credenciales incorrectas.");

            return Ok(new AuthResponseDto { 
                Id = usuario.Id, 
                Email = usuario.Email, 
                Mensaje = "Inicio de sesión correcto" 
            });
        }
    }
}