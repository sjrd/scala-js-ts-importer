declare module A {
  interface Info {
    settings: {
      state: {
        enable: boolean;
      };
    };
  }

  export let objectInfo: Info;
}
