import { useEffect, useState } from "react";

export function useAsync(asyncFn, deps) {
  const [state, setState] = useState({
    data: null,
    loading: true,
    error: null
  });

  useEffect(() => {
    let active = true;
    setState({ data: null, loading: true, error: null });
    asyncFn()
      .then((data) => {
        if (active) {
          setState({ data, loading: false, error: null });
        }
      })
      .catch((error) => {
        if (active) {
          setState({ data: null, loading: false, error: error.message });
        }
      });
    return () => {
      active = false;
    };
  }, deps);

  return state;
}
