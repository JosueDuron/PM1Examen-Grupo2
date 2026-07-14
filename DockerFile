
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build-env
WORKDIR /app


COPY . ./
RUN dotnet restore


RUN dotnet publish src/ExamenApi.API/ExamenApi.API.csproj -c Release -o out


FROM mcr.microsoft.com/dotnet/aspnet:8.0
WORKDIR /app
COPY --from=build-env /app/out .


ENV ASPNETCORE_URLS=http://+:4445
EXPOSE 4445

ENTRYPOINT ["dotnet", "ExamenApi.API.dll"]