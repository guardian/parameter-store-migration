class Configuration {
  public stringProperty: string = "Hello, world";
  public "nested" = new class {
    public numberProperty: number = 10;
    public anotherStringProperty: string = "Hello, worlds";
  }
}

const config: Configuration = new Configuration();
console.log(JSON.stringify(config))
