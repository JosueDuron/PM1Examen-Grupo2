using ExamenApi.Application.DTOs;
using ExamenApi.Application.Interfaces;
using ExamenApi.Domain.Entities;
using ExamenApi.Domain.Interfaces;

namespace ExamenApi.Application.Services
{
    public class ContactoService : IContactoService
    {
        private readonly IContactoRepository _repository;

        public ContactoService(IContactoRepository repository)
        {
            _repository = repository;
        }

        public async Task<IEnumerable<ContactoResponseDto>> ObtenerTodosAsync(int usuarioId)
        {
            var contactos = await _repository.ObtenerTodosAsync(usuarioId); 
            return contactos.Select(c => MapToResponseDto(c));
        }

        public async Task<ContactoResponseDto?> ObtenerPorIdAsync(int id)
        {
            var contacto = await _repository.ObtenerPorIdAsync(id);
            return contacto == null ? null : MapToResponseDto(contacto);
        }

        public async Task<IEnumerable<ContactoResponseDto>> BuscarPorTextoAsync(string texto, int usuarioId)
        {
            var contactos = await _repository.BuscarAsync(texto, usuarioId); 
            return contactos.Select(c => MapToResponseDto(c)); 
        }

        public async Task<ContactoResponseDto> CrearAsync(CrearContactoDto dto)
        {
            var contacto = new Contacto
            {
                Nombre = dto.Nombre,
                Telefono = dto.Telefono,
                Direccion = dto.Direccion,
                Latitud = dto.Latitud,
                Longitud = dto.Longitud,
                FirmaBase64 = dto.FirmaBase64,
                ImagenBase64 = dto.ImagenBase64,
                UsuarioId = dto.UsuarioId 
            };

            var creado = await _repository.AgregarAsync(contacto);
            return MapToResponseDto(creado);
        }

        public async Task<bool> ActualizarAsync(ActualizarContactoDto dto)
        {
            var contactoExistente = await _repository.ObtenerPorIdAsync(dto.Id);
            if (contactoExistente == null) return false;

            contactoExistente.Nombre = dto.Nombre;
            contactoExistente.Telefono = dto.Telefono;
            contactoExistente.Direccion = dto.Direccion;
            contactoExistente.Latitud = dto.Latitud;
            contactoExistente.Longitud = dto.Longitud;
            contactoExistente.FirmaBase64 = dto.FirmaBase64;
            contactoExistente.ImagenBase64 = dto.ImagenBase64;
            contactoExistente.UsuarioId = dto.UsuarioId; 

            return await _repository.ActualizarAsync(contactoExistente);
        }

        public async Task<bool> EliminarAsync(int id) => await _repository.EliminarAsync(id);

        private static ContactoResponseDto MapToResponseDto(Contacto c) => new()
        {
            Id = c.Id,
            Nombre = c.Nombre,
            Telefono = c.Telefono,
            Direccion = c.Direccion,
            Latitud = c.Latitud,
            Longitud = c.Longitud,
            FirmaBase64 = c.FirmaBase64,
            ImagenBase64 = c.ImagenBase64,
            FechaCreacion = c.FechaCreacion,
            UsuarioId = c.UsuarioId
        };
    }
}