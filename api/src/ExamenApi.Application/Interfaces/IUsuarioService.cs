using ExamenApi.Application.DTOs;

namespace ExamenApi.Application.Interfaces
{
    public interface IUsuarioService
    {
        Task<UsuarioResponseDto?> GetByIdAsync(int id);
        Task<IEnumerable<UsuarioResponseDto>> GetAllAsync();
        Task<AuthResponseDto> RegisterAsync(RegistrarUsuarioDto dto);
        Task<AuthResponseDto?> LoginAsync(LoginUsuarioDto dto);
        Task<UsuarioResponseDto> UpdateAsync(ActualizarUsuarioDto dto);
        Task SoftDeleteAsync(int id);
    }
}
