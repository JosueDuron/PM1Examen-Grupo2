using ExamenApi.Application.DTOs;

namespace ExamenApi.Application.Interfaces
{
    public interface IContactoService
    {
        Task<IEnumerable<ContactoResponseDto>> ObtenerTodosAsync(int usuarioId); 
        Task<ContactoResponseDto?> ObtenerPorIdAsync(int id);
        Task<IEnumerable<ContactoResponseDto>> BuscarPorTextoAsync(string texto, int usuarioId); 
        
        Task<ContactoResponseDto> CrearAsync(CrearContactoDto dto);
        Task<bool> ActualizarAsync(ActualizarContactoDto dto);
        Task<bool> EliminarAsync(int id);
    }
}