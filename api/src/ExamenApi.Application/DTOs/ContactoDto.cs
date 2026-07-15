namespace ExamenApi.Application.DTOs
{
    public class CrearContactoDto
    {
        public string Nombre { get; set; } = string.Empty;
        public string Telefono { get; set; } = string.Empty;
        public string Direccion { get; set; } = string.Empty;
        public double Latitud { get; set; }
        public double Longitud { get; set; }
        public string? FirmaBase64 { get; set; }
        public string? ImagenBase64 { get; set; }
        public int UsuarioId { get; set; }
    }

    public class ActualizarContactoDto : CrearContactoDto
    {
        public int Id { get; set; }
    }

    public class ContactoResponseDto : ActualizarContactoDto
    {
        public DateTime FechaCreacion { get; set; }
        public int Status { get; set; }
    }
}
