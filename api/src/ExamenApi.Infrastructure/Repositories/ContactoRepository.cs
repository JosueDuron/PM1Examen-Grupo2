using ExamenApi.Domain.Entities;
using ExamenApi.Domain.Interfaces;
using ExamenApi.Infrastructure.Data;
using Microsoft.EntityFrameworkCore;

namespace ExamenApi.Infrastructure.Repositories
{
    public class ContactoRepository : IContactoRepository
    {
        private readonly AppDbContext _context;

        public ContactoRepository(AppDbContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<Contacto>> ObtenerTodosAsync(int usuarioId) =>
            await _context.Contactos
                .Where(c => c.UsuarioId == usuarioId)
                .OrderByDescending(c => c.FechaCreacion)
                .ToListAsync();

        public async Task<Contacto?> ObtenerPorIdAsync(int id) =>
            await _context.Contactos.FindAsync(id);

        public async Task<IEnumerable<Contacto>> BuscarAsync(string texto, int usuarioId) =>
            await _context.Contactos
                .Where(c => c.UsuarioId == usuarioId && (c.Nombre.Contains(texto) || c.Direccion.Contains(texto)))
                .ToListAsync();

        public async Task<Contacto> AgregarAsync(Contacto contacto)
        {
            await _context.Contactos.AddAsync(contacto);
            await _context.SaveChangesAsync();
            return contacto;
        }

        public async Task<bool> ActualizarAsync(Contacto contacto)
        {
            _context.Contactos.Update(contacto);
            return await _context.SaveChangesAsync() > 0;
        }

        public async Task<bool> SoftDeleteAsync(int id)
        {
            var contacto = await _context.Contactos.FindAsync(id);
            if (contacto == null) return false;
            contacto.Status = 0;
            return await _context.SaveChangesAsync() > 0;
        }
    }
}
