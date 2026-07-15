namespace ExamenApi.Domain.Entities
{
    public class Contacto
    {
        public int Id { get; set; }
        public string Nombre { get; set; } = string.Empty;
        public string Telefono { get; set; } = string.Empty;
        public string Direccion { get; set; } = string.Empty;
        public double Latitud { get; set; }
        public double Longitud { get; set; }
        public string? FirmaBase64 { get; set; }
        public string? ImagenBase64 { get; set; }
        public DateTime FechaCreacion { get; set; } = DateTime.UtcNow;
        public int Status { get; set; } = 1; // 1 = Activo, 0 = Eliminado

        public int UsuarioId { get; set; }
        public Usuario? Usuario { get; set; }
    }
}
