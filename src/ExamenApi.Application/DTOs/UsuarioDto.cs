namespace ExamenApi.Application.DTOs
{
    public class RegistrarUsuarioDto
    {
        public string Email { get; set; } = string.Empty;
        public string Password { get; set; } = string.Empty;
    }

    public class LoginUsuarioDto
    {
        public string Email { get; set; } = string.Empty;
        public string Password { get; set; } = string.Empty;
    }

    public class AuthResponseDto
    {
        public int Id { get; set; }
        public string Email { get; set; } = string.Empty;
        public string Mensaje { get; set; } = string.Empty;
    }
}