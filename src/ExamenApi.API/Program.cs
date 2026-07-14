using Microsoft.EntityFrameworkCore;
using ExamenApi.Application.Interfaces;
using ExamenApi.Application.Services;
using ExamenApi.Domain.Interfaces;
using ExamenApi.Infrastructure.Data;
using ExamenApi.Infrastructure.Repositories;

var builder = WebApplication.CreateBuilder(args);

var connectionString = builder.Configuration.GetConnectionString("DefaultConnection");

builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseMySql(connectionString, ServerVersion.AutoDetect(connectionString)));

builder.Services.AddScoped<IContactoRepository, ContactoRepository>();
builder.Services.AddScoped<IContactoService, ContactoService>();

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddCors(options =>
{
    options.AddPolicy("PermitirTodo", policy =>
        policy.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader());
});

var app = builder.Build();

app.UseSwagger();
app.UseSwaggerUI();
app.UseCors("PermitirTodo");
app.UseAuthorization();
app.MapControllers();

app.Run("http://0.0.0.0:4445");