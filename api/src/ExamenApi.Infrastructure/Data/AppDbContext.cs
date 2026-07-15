using Microsoft.EntityFrameworkCore;
using ExamenApi.Domain.Entities;

namespace ExamenApi.Infrastructure.Data
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

        public DbSet<Contacto> Contactos { get; set; }
        public DbSet<Usuario> Usuarios { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Usuario>(entity =>
            {
                entity.ToTable("Usuarios");
                entity.HasKey(u => u.Id);
                entity.HasIndex(u => u.Email).IsUnique();
                entity.Property(u => u.Email).IsRequired().HasMaxLength(150);
                entity.Property(u => u.PasswordHash).IsRequired().HasMaxLength(255);
                entity.Property(u => u.FechaRegistro).HasColumnType("datetime(6)");
                entity.Property(u => u.Status).HasDefaultValue(1);
                entity.HasQueryFilter(u => u.Status == 1); // Solo activos
            });

            modelBuilder.Entity<Contacto>(entity =>
            {
                entity.ToTable("Contactos");
                entity.HasKey(c => c.Id);
                entity.Property(c => c.Nombre).IsRequired().HasMaxLength(150);
                entity.Property(c => c.FirmaBase64).HasColumnType("LONGTEXT");
                entity.Property(c => c.ImagenBase64).HasColumnType("LONGTEXT");
                entity.Property(c => c.FechaCreacion).HasColumnType("datetime(6)");
                entity.Property(c => c.Status).HasDefaultValue(1);
                entity.HasQueryFilter(c => c.Status == 1); // Solo activos
                entity.HasOne(c => c.Usuario)
                      .WithMany(u => u.Contactos)
                      .HasForeignKey(c => c.UsuarioId)
                      .OnDelete(DeleteBehavior.Cascade);
            });
        }
    }
}
