FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build-env
WORKDIR /app

COPY src/ExamenApi.Domain/*.csproj ./src/ExamenApi.Domain/
COPY src/ExamenApi.Application/*.csproj ./src/ExamenApi.Application/
COPY src/ExamenApi.Infrastructure/*.csproj ./src/ExamenApi.Infrastructure/
COPY src/ExamenApi.API/*.csproj ./src/ExamenApi.API/
COPY nuget.config ./
COPY ExamenApi.sln ./

RUN dotnet restore src/ExamenApi.API/ExamenApi.API.csproj

COPY src/ExamenApi.Domain/. ./src/ExamenApi.Domain/
COPY src/ExamenApi.Application/. ./src/ExamenApi.Application/
COPY src/ExamenApi.Infrastructure/. ./src/ExamenApi.Infrastructure/
COPY src/ExamenApi.API/. ./src/ExamenApi.API/

RUN dotnet publish src/ExamenApi.API/ExamenApi.API.csproj -c Release -o out

FROM mcr.microsoft.com/dotnet/aspnet:8.0
WORKDIR /app
COPY --from=build-env /app/out .

ENV ASPNETCORE_URLS=http://+:10000
EXPOSE 10000

ENTRYPOINT ["dotnet", "ExamenApi.API.dll"]
