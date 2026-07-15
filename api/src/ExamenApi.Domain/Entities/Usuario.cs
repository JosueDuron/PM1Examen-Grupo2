namespace ExamenApi.Domain.Entities
{
    public class Usuario
    {
        public int Id { get; set; }
        public string Email { get; set; } = string.Empty;
        public string PasswordHash { get; set; } = string.Empty;
        public DateTime FechaRegistro { get; set; } = DateTime.UtcNow;
        public int Status { get; set; } = 1; // 1 = Activo, 0 = Eliminado
        public ICollection<Contacto> Contactos { get; set; } = new List<Contacto>();
    }
}
