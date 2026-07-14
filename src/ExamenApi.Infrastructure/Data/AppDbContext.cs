using Microsoft.EntityFrameworkCore;
using ExamenApi.Domain.Entities;

namespace ExamenApi.Infrastructure.Data
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

        public DbSet<Contacto> Contactos { get; set; }
        public DbSet<Usuario> Usuarios { get; set; } // Nueva tabla normalizada

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Usuario>(entity =>
            {
                entity.ToTable("Usuarios");
                entity.HasKey(u => u.Id);
                entity.HasIndex(u => u.Email).IsUnique(); // El email no se puede repetir
                entity.Property(u => u.Email).IsRequired().HasMaxLength(150);
                entity.Property(u => u.PasswordHash).IsRequired().HasMaxLength(255);
            });

            modelBuilder.Entity<Contacto>(entity =>
            {
                entity.ToTable("Contactos");
                entity.HasKey(c => c.Id);
                entity.Property(c => c.Nombre).IsRequired().HasMaxLength(150);
                entity.Property(c => c.FirmaBase64).HasColumnType("LONGTEXT");
                entity.Property(c => c.ImagenBase64).HasColumnType("LONGTEXT");
                entity.HasOne(c => c.Usuario)
                      .WithMany(u => u.Contactos)
                      .HasForeignKey(c => c.UsuarioId)
                      .OnDelete(DeleteBehavior.Cascade); 
            });
        }
    }
}