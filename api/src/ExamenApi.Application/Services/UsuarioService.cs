using ExamenApi.Application.DTOs;
using ExamenApi.Application.Interfaces;
using ExamenApi.Domain.Entities;
using ExamenApi.Domain.Interfaces;

namespace ExamenApi.Application.Services
{
    public class UsuarioService : IUsuarioService
    {
        private readonly IUsuarioRepository _usuarioRepository;
        private readonly IPasswordHasher _passwordHasher;
        private readonly IJwtService _jwtService;

        public UsuarioService(IUsuarioRepository usuarioRepository, IPasswordHasher passwordHasher, IJwtService jwtService)
        {
            _usuarioRepository = usuarioRepository;
            _passwordHasher = passwordHasher;
            _jwtService = jwtService;
        }

        public async Task<UsuarioResponseDto?> GetByIdAsync(int id)
        {
            var usuario = await _usuarioRepository.GetByIdAsync(id);
            return usuario == null ? null : MapToDto(usuario);
        }

        public async Task<IEnumerable<UsuarioResponseDto>> GetAllAsync()
        {
            var usuarios = await _usuarioRepository.GetAllAsync();
            return usuarios.Select(MapToDto);
        }

        public async Task<AuthResponseDto> RegisterAsync(RegistrarUsuarioDto dto)
        {
            var existing = await _usuarioRepository.GetByEmailAsync(dto.Email);
            if (existing != null)
                throw new Exception("El email ya esta registrado");

            var usuario = new Usuario
            {
                Email = dto.Email,
                PasswordHash = _passwordHasher.HashPassword(dto.Password),
                FechaRegistro = DateTime.UtcNow,
                Status = 1
            };

            var result = await _usuarioRepository.AddAsync(usuario);
            var token = _jwtService.GenerateToken(result.Id, result.Email);

            return new AuthResponseDto
            {
                Id = result.Id,
                Email = result.Email,
                Token = token,
                Mensaje = "Usuario creado exitosamente"
            };
        }

        public async Task<AuthResponseDto?> LoginAsync(LoginUsuarioDto dto)
        {
            var usuario = await _usuarioRepository.GetByEmailAsync(dto.Email);
            if (usuario == null || !_passwordHasher.VerifyPassword(dto.Password, usuario.PasswordHash))
                return null;

            if (usuario.Status != 1)
                return null;

            var token = _jwtService.GenerateToken(usuario.Id, usuario.Email);

            return new AuthResponseDto
            {
                Id = usuario.Id,
                Email = usuario.Email,
                Token = token,
                Mensaje = "Inicio de sesion correcto"
            };
        }

        public async Task<UsuarioResponseDto> UpdateAsync(ActualizarUsuarioDto dto)
        {
            var usuario = await _usuarioRepository.GetByIdAsync(dto.Id);
            if (usuario == null) throw new Exception("Usuario no encontrado");

            usuario.Email = dto.Email;
            usuario.Status = dto.Status;
            await _usuarioRepository.UpdateAsync(usuario);
            return MapToDto(usuario);
        }

        public async Task SoftDeleteAsync(int id)
        {
            await _usuarioRepository.SoftDeleteAsync(id);
        }

        private static UsuarioResponseDto MapToDto(Usuario usuario) => new()
        {
            Id = usuario.Id,
            Email = usuario.Email,
            FechaRegistro = usuario.FechaRegistro,
            Status = usuario.Status
        };
    }
}
