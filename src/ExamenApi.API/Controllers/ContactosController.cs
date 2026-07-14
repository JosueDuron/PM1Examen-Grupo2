using Microsoft.AspNetCore.Mvc;
using ExamenApi.Application.DTOs;
using ExamenApi.Application.Interfaces;

namespace ExamenApi.API.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class ContactosController : ControllerBase
    {
        private readonly IContactoService _service;

        public ContactosController(IContactoService service)
        {
            _service = service;
        }

        [HttpGet]
        public async Task<IActionResult> ObtenerTodos([FromQuery] int usuarioId)
        {
            if (usuarioId <= 0) return BadRequest("Debe proporcionar un ID de usuario válido.");
            return Ok(await _service.ObtenerTodosAsync(usuarioId));
        }

        [HttpGet("{id}")]
        public async Task<IActionResult> ObtenerPorId(int id)
        {
            var contacto = await _service.ObtenerPorIdAsync(id);
            if (contacto == null) return NotFound($"No existe un contacto con el ID {id}");
            return Ok(contacto);
        }

        // CORREGIDO: Ahora acepta y transmite el usuarioId para el filtrado en la BD
        [HttpGet("buscar")]
        public async Task<IActionResult> Buscar([FromQuery] string texto, [FromQuery] int usuarioId)
        {
            if (string.IsNullOrWhiteSpace(texto)) return BadRequest("Debe enviar un texto para buscar.");
            if (usuarioId <= 0) return BadRequest("Debe proporcionar un ID de usuario válido.");
            return Ok(await _service.BuscarPorTextoAsync(texto, usuarioId));
        }

        [HttpPost]
        public async Task<IActionResult> Crear([FromBody] CrearContactoDto dto)
        {
            if (dto.UsuarioId <= 0) return BadRequest("El contacto debe estar asociado a un ID de usuario válido.");
            var creado = await _service.CrearAsync(dto);
            return CreatedAtAction(nameof(ObtenerPorId), new { id = creado.Id }, creado);
        }

        [HttpPut] 
        public async Task<IActionResult> Actualizar([FromBody] ActualizarContactoDto dto)
        {
            var actualizado = await _service.ActualizarAsync(dto);
            if (!actualizado) return NotFound($"No existe un contacto con el ID {dto.Id}");
            return Ok("Contacto actualizado correctamente.");
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Eliminar(int id)
        {
            var eliminado = await _service.EliminarAsync(id);
            if (!eliminado) return NotFound($"No existe un contacto con el ID {id}");
            return Ok("Contacto eliminado correctamente.");
        }
    }
}