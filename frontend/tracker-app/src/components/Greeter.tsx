interface GreeterProps {
  person: string;
}
const Greeter = (props: GreeterProps) => {
  return <h1>hello! {props.person}</h1>;
};
export default Greeter;
