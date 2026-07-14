using ExamenApi.Domain.Entities;

namespace ExamenApi.Domain.Interfaces
{
    public interface IContactoRepository
    {
        Task<IEnumerable<Contacto>> ObtenerTodosAsync(int usuarioId);
        Task<Contacto?> ObtenerPorIdAsync(int id);
        Task<IEnumerable<Contacto>> BuscarAsync(string texto, int usuarioId);
        Task<Contacto> AgregarAsync(Contacto contacto);
        Task<bool> ActualizarAsync(Contacto contacto);
        Task<bool> EliminarAsync(int id);
    }
}